(ns adequate-stage.global-dash
  (:require-macros [adequate-stage.macros :refer [inspect]]
                   [adequate-stage.material :as mui]
                   )

  (:require
   [adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
   [adequate-stage.metrics :as met]
   [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(def metrics-data
  {:columns ["status" "x" "campaign" "network" "account"]
   :rows [["Active" "x" "Foo" "Twitter" "Kabir"]]})

(def filters
  {:values ["all_visible" "all_active" "all_with_deleted" "all_inactive"]
   :texts ["All But Deleted" "All Active" "All" "Paused/Completed"]})


(def table-row-column js/window.MaterialUI.TableRowColumn)

(defn data->table-row [row]
  (js/React.createElement
   js/window.MaterialUI.TableRow
   nil
   (->> row
        (map #(js/React.createElement table-row-column nil %)))))

(def table-header-column js/window.MaterialUI.TableHeaderColumn)

(defn data->table-header [{columns :columns}]
  (mui/table-header
   (js/React.createElement
    js/window.MaterialUI.TableRow
    nil
    (->> columns
         (map clojure.string/upper-case)
         (map #(js/React.createElement table-header-column nil %))))))


(defcs metrics-table < (rum/local nil) [state data]
  (let [rows* (map data->table-row (:rows data))
        rows (->> rows* (repeat 5) flatten)]
    (time (mui/table
      {:fixedHeader     false
       :height          "270px"
       :fixedFooter     true
       :footerDataGetter #(["14" "15124" "12514" "12414" "1241"])
       :multiSelectable true}
      (data->table-header data)
      (js/React.createElement
       js/window.MaterialUI.TableBody
       #js {:deselectOnClickAway false
            :showRowHover        true
            :stripedRows         true
            :preScanRows         false}
       rows)
       (js/React.createElement
        js/window.MaterialUI.TableFooter
        (data->table-row ["123" "121242" "124" "1242"])     )))))

(defcs filter-dropdown [state]
  (let [menuItems (mapv (fn [v1 v2] {:payload v1 :text v2}) (:values filters) (:texts filters))]
    (mui/drop-down-menu {:menuItems menuItems})))

(def paging-sizes [10 25 50 100])

(defcs paging-dropdown [state page-size]
  (let [menuItems (mapv (fn [v1] {:payload v1 :text v1}) paging-sizes)
        selectedIndex (.indexOf (to-array paging-sizes) page-size)]
    (mui/drop-down-menu {:style {:width "90px" } :selectedIndex selectedIndex :autoWidth false :menuItems menuItems})))

(defcs paging [state db]
  (let [page-number (or (system-attr db :page-number) 1)
        page-size (or (system-attr db :page-size) 50)]
    [:div
      [:div.col.span_1_of_4
        "GO TO PAGE:" [:input.paging-input {:value page-number}]]
      [:div.col.span_1_of_4
       "NUMBER OF ROWS:" (paging-dropdown page-size)]
      [:div.col
       [:button.paging-button "<"]
       "1-10 of 10"
       [:button.paging-button ">"]]
     ])
)

(defcs global-dash [state db]
  [:div
   [:h1 "Welcome to AdequateStage!"]

   [:div.section.group
    [:div.col.span_1_of_3
      (filter-dropdown state)]
     [:div.col.span_1_of_3
    [:div.col.span_1_of_3
     (mui/date-picker
      {:hintText "Start"
       :showYearSelector true})]
    [:div.col.span_1_of_3
     (mui/date-picker
      {:hintText "End"})]]]
     [:div.col.span_1_of_3
        ]

   [:div.section.group
    [:div.col.span_1_of_1
     ]
    ]

   [:div.section.group
    [:div.col.span_1_of_10]
    [:div.col.span_8_of_10
     (metrics-table metrics-data)
     ]
    [:div.col.span_1_of_10]]

    [:div.section.group
     [:div.col.span_2_of_5]
     [:div.col.span_3_of_5
      (paging db)
     ]
    ]
   ]
  )

