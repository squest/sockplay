(ns sockplay.handler
  (:use [compojure.route :only [files not-found resources]]
        [compojure.handler :only [site]] ; form, query params decode; cookie; session, etc
        [compojure.core :only [defroutes GET POST DELETE ANY context]]
        org.httpkit.server)
  (:require [selmer.parser :as page]
            [com.ashafa.clutch :as cl]
            [couchbase-clj.client :as cc]
            [cheshire.core :as cs]
            [ring.middleware.session :as session]))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(def ^:private chatrooms
  "List of chatrooms available on server, chatroom's name as key and
  the number of users in chatroom as value"
  (atom {}))

(def channels
  "Global atom that consists of all channels in the machine, it is a vector of channel maps,
  each channel map contains {:username [string] :chatroom [string] :channel [native http-kit channel]}"
  (atom []))

(def ^:private current-user
  "A helper atom to represents the newly join user, I dont yet know how to pass this kind of info from http
  session through websocket, thus an ugly implementation using this module-wide atom"
  (atom ""))

(defn- send-message-to-room
  "Accepts data sent by an individual user, and will echo the data to all users subscribed to a
  particular chatroom in which the user is send the data from"
  [data]
  (let [map-data (cs/parse-string data true)
        room-channel (filter #(= (:chatroom map-data)
                                 (:chatroom %))
                             @channels)]
    (doseq [ch room-channel]
      (send! (:channel ch)
             (-> (assoc map-data :type "message")
                 (cs/generate-string))
             false))))

(defn- on-receive-data
  "A callback function to handle on-receive websocket event, data is the data sent by a particular user,
  a typical JSON file as the front-end is (still) in Angular, I use cond just in case we have other
  data types other than just 'message'"
  [data]
  (let [chan-data (cs/parse-string data true)]
    (cond (= "message" (:dataType chan-data))
          (send-message-to-room data))))

(defn- on-open
  "A callback to handle on channel open event, which is an event where a single user open a connection to
  the server from the browser, channel is the default channel data passed from http-kit"
  [channel]
  (if (websocket? channel)
      (do (swap! channels conj (assoc @current-user
                               :channel channel))
          (let [chans (filter #(= (:chatroom @current-user)
                                  (:chatroom %))
                              @channels)]
            (doseq [ch chans]
              (send! (:channel ch)
                     (-> {:type "users"
                          :list (-> #(dissoc % :channel)
                                    (map chans))}
                         (cs/generate-string))
                     false))))
      (do (println "HTTP channel")
          (cs/generate-string {:status false :message "Sorry, no http served on this page"}))))


(defn handler
  "Handler for the websocket connection, this is a default template from http-kit, I still can't find
  a way to make on-close function that can handle this event from outside the handler, it seems do not
  work from outside the handler"
  ;TODO how to make the on-close channel from outside the handler
  [req]
  (with-channel req channel           ; get the channel
                (on-close channel
                          (fn [status]
                            (let [chatroom (filter #(= channel
                                                       (:channel %))
                                                   @channels)]
                              (do (reset! channels
                                          (vec (remove #(= channel (:channel %)) @channels)))
                                  (if (= 1 (get @chatrooms (:chatroom chatroom)))
                                      (swap! chatrooms
                                             dissoc
                                             (:chatroom chatroom))
                                      (swap! chatrooms
                                             assoc
                                             (:chatroom chatroom)
                                             (inc (get @chatrooms (:chatroom chatroom)))))
                                  (let [chans (filter #(= (:chatroom chatroom)
                                                          (:chatroom %))
                                                      @channels)]
                                    (doseq [ch chans]
                                      (send! (:channel ch)
                                             (-> {:type "users"
                                                  :list (-> #(dissoc % :channel)
                                                            (map chans))}
                                                 (cs/generate-string))
                                             false)))))))
                (on-open channel)
                (on-receive channel on-receive-data)))

(defn homepage [req]
  (page/render-file "public/home.html"
                    {:page {:title "Welcome"
                            :headline "Hello Jon"}}))

(defn chatpage [usermap]
  (page/render-file "public/chat.html"
                    {:page {:title "Let's chat"
                            :headline "Hello jon"}
                     :user usermap}))

(defroutes all-routes
           (GET "/" [req] homepage)
           (POST "/login" req
                 (do (println req)
                     (let [user (:username (:params req))
                           chatroom (:chatroom (:params req))]
                       (do (reset! current-user {:username (str user)
                                                 :chatroom (str chatroom)})
                           (swap! chatrooms assoc
                                            (:chatroom (:params req))
                                            (inc (get @chatrooms
                                                      (:chatroom (:params req))
                                                      0)))
                           (chatpage {:username (str user)
                                      :chatroom (str chatroom)})))))
           (GET "/ws" req
                (do (println req)
                    (handler req)))       ;; websocket
           (files "/" {:root "resources/public/"}) ;; static file url prefix /static, in `public` folder
           (not-found "<p>Page not found.</p>")
           (resources "/" {:root "resources/public/"})) ;; all other, return 404

(defn -main [& args]
  ;; The #' is useful, when you want to hot-reload code
  ;; You may want to take a look: https://github.com/clojure/tools.namespace
  ;; and http://http-kit.org/migration.html#reload
  (reset! server (run-server (site #'all-routes) {:port 3000}))
  (println "Server is running at port 3000"))
