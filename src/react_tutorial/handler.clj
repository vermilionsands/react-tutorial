(ns react-tutorial.handler
  (:require [compojure.core :refer :all]
            [clojure.data.json :as json]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [content-type response header file-response]]))

(def resources "resources/public/")

(defroutes app-routes
  (GET "/" []
    (-> (file-response "index.html" {:root resources})
        (content-type "text/html")))
  (ANY "/api/comments" req
    (let [action (:request-method req)
          comments (cond->
                     (json/read-str (slurp "comments.json"))
                     (= :post action) (conj (assoc (:params req) "id" (System/currentTimeMillis))))
          json-comments (json/write-str comments)]
      (spit "comments.json" json-comments)
      (-> (response json-comments)
          (header "Cache-Control" "no-cache")
          (content-type "application/json")))))

(def app (wrap-defaults app-routes (assoc site-defaults :security false)))
