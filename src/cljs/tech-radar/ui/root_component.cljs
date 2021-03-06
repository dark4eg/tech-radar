(ns tech-radar.ui.root-component
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [om.next :as om]
            [tech-radar.ui.navbar :refer [nav-bar
                                          NavBar]]
            [tech-radar.ui.topic-view :refer [topic-view
                                              TopicView]]
            [tech-radar.ui.trends-view :refer [trends-view
                                               TrendsView]]
            [tech-radar.ui.home :refer [home]]))

(defui RootComponent
  static om/IQuery
  (query [this]
    `[{:settings ~(om/get-query NavBar)}                    ; Construct cursor for NavBar
      :trend-type
      :current-screen                                       ; Required for dispatching main view
      :current-topic                                        ; For mixing
      :state                                                ; Because TopicView & TrendsView required full state
      :statistic
      ])
  Object
  (set-page-number [this cnt]
    (om/transact! this `[(page-number/set {:page-number ~cnt}) [:settings]]))
  (set-trend-type [this trend-type]
    (om/transact! this `[(trend-type/set {:trend-type ~trend-type}) [:settings]]))
  (set-current-trend [this current-trend]
    (om/transact! this `[(current-trend/set {:current-trend ~current-trend}) [:settings]]))

  (render [this]
    (let [{:keys [settings current-screen current-topic statistic state]} (om/props this)]
      (html
        [:div#wrapper {:class (when-not (or (= current-screen :home)
                                            (= current-screen :trends))
                                "top-shifted")}
         (nav-bar (om/computed settings
                               {:current-screen current-screen
                                :current-topic  current-topic}))
         [:div#page-wrapper {}
          (condp = current-screen
            :home (home (om/computed {} {:statistic statistic}))
            :trends (trends-view (om/computed state {:set-trend-type    #(.set-trend-type this %)
                                                     :set-current-trend #(.set-current-trend this %)}))
            (topic-view (om/computed state
                                     {:set-page-number
                                      (fn [page-number]
                                        #(.set-page-number this page-number))})))]]))))
