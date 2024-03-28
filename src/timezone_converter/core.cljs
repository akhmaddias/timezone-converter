(ns timezone-converter.core
  (:require
   ["dayjs" :as dayjs]
   ["dayjs/plugin/timezone" :as timezone]
   ["dayjs/plugin/utc" :as utc]
   ["react-select" :default ReactSelect]
   [reagent.core :as r]
   [reagent.dom :as d]))

(.extend dayjs utc)
(.extend dayjs timezone)

(def input-datetime (r/atom nil))
(def input-timezone (r/atom nil))
(def output-timezone (r/atom nil))
(def output-datetime (r/atom nil))


(def timezones (map (fn [timezone]
                      {:label timezone
                       :value timezone})
                    (.supportedValuesOf js/Intl "timeZone")))

(defn handle-timezone-convert
  []
  (let [input-time (dayjs/tz @input-datetime @input-timezone)]
    (reset! output-datetime (-> input-time
                                (.tz @output-timezone)
                                (.format "DD.MM.YYYY, HH:mm Z")))))

(defn home-page []
  [:div.container
   [:input.datetime
    {:placeholder "Input DateTime"
     :type "datetime-local"
     :value @input-datetime
     :on-change #(reset! input-datetime (-> % .-target .-value))}]
   [:> ReactSelect {:options timezones
                    :placeholder "Input Timezone"
                    :searchable true
                    :on-change #(reset! input-timezone
                                        (-> (js->clj % :keywordize-keys true)
                                            :value))}]
   [:> ReactSelect {:options timezones
                    :placeholder "Output Timezone"
                    :searchable true
                    :on-change #(reset! output-timezone
                                        (-> (js->clj % :keywordize-keys true)
                                            :value))}] 
   [:button.convert
    {:on-click handle-timezone-convert}
    "Convert"]
   [:span @output-datetime]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
