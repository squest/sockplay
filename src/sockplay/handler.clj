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

(def channels (atom []))
(def current-user (atom ""))

(defn on-receive-message
  [data]
  (loop [ch @channels]
    (if (empty? ch)
      (do (println @channels))
      (recur (do (send! (:channel (first ch))
                        (let [map-data (cs/parse-string data true)]
                          (-> (assoc map-data :type "message")
                              (cs/generate-string)))
                        false)
                 (rest ch))))))

(defn on-receive-data
  [data]
  (let [chan-data (cs/parse-string data true)]
    (cond (= "message" (:dataType chan-data))
          (on-receive-message data))))

(defn on-channel-close [channel]
  (fn [status]
      (do (reset! channels
                  (vec (remove #(= channel (:channel %)) @channels)))
          (loop [ch @channels]
            (if (empty? ch)
                (do (println @channels))
                (recur (do (send! (:channel (first ch))
                                  (-> {:type "new-user"
                                       :list (-> #(dissoc % :channel)
                                                 (map @channels))}
                                      (cs/generate-string))
                                  false)
                           (rest ch))))))))

(defn on-open
  [channel]
  (if (websocket? channel)
      (do (println "WebSocket channel")
          (swap! channels conj {:user @current-user
                                :channel channel})
          (loop [ch @channels]
            (if (empty? ch)
                (do (println @channels))
                (recur (do (send! (:channel (first ch))
                                  (-> {:type "new-user"
                                       :list (-> #(dissoc % :channel)
                                                 (map @channels))}
                                      (cs/generate-string))
                                  false)
                           (rest ch))))))
      (println "HTTP channel")))

(defn handler [req]
  (with-channel req channel              ; get the channel
                                         ;; communicate with client using method defined above
                (on-close channel on-channel-close)
                (on-open channel)
                (on-receive channel on-receive-data)))

(defn homepage [req]
  (page/render-file "public/home.html"
                    {:page {:title "Welcome"
                            :headline "Hello Jon"}}))

(defn chatpage [user]
  (page/render-file "public/chat.html"
                    {:page {:title "Let's chat"
                            :headline "Hello joon"}
                     :user user}))

(defroutes all-routes
           (GET "/" [req] homepage)
           (POST "/login" req
                 (do (println req)
                     (let [user (:username (:params req))]
                       (do (reset! current-user (str user))
                           (chatpage (str user))))))
           (GET "/chat" req
                (do (println req)
                    (chatpage (:user (:my-session req)))))
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
