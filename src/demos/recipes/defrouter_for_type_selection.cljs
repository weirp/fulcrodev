(ns recipes.defrouter-for-type-selection
  (:require
    [om.dom :as dom]
    [fulcro.client.routing :as r :refer [defrouter]]
    [om.next :as om :refer [defui]]
    [fulcro.client.core :as fc]
    [fulcro.ui.bootstrap3 :as b]
    [fulcro.ui.elements :as ele]))

(defn item-ident
  "Generate an ident from a person, place, or thing."
  [props] [(:kind props) (:db/id props)])

(defn item-key
  "Generate a distinct react key for a person, place, or thing"
  [props] (str (:kind props) "-" (:db/id props)))

(defn make-person [id n] {:db/id id :kind :person :person/name n})
(defn make-place [id n] {:db/id id :kind :place :place/name n})
(defn make-thing [id n] {:db/id id :kind :thing :thing/label n})

(defui ^:once PersonDetail
  static om/Ident
  (ident [this props] (item-ident props))
  static om/IQuery
  (query [this] [:kind :db/id :person/name])
  ; defrouter expects there to be an initial state for each possible target. We'll cause this to be a "no selection"
  ; state so that the detail screen that starts out will show "Nothing selected". We initialize all three in case
  ; we later re-order them in the defrouter.
  static fc/InitialAppState
  (initial-state [c p] {:db/id :no-selection :kind :person})
  Object
  (render [this]
    (let [{:keys [db/id person/name]} (om/props this)]
      (dom/div nil
        (if (= id :no-selection)
          "Nothing selected"
          (str "Details about person " name))))))

(defui ^:once PlaceDetail
  static om/Ident
  (ident [this props] (item-ident props))
  static om/IQuery
  (query [this] [:kind :db/id :place/name])
  static fc/InitialAppState
  (initial-state [c p] {:db/id :no-selection :kind :place})
  Object
  (render [this]
    (let [{:keys [db/id place/name]} (om/props this)]
      (dom/div nil
        (if (= id :no-selection)
          "Nothing selected"
          (str "Details about place " name))))))

(defui ^:once ThingDetail
  static om/Ident
  (ident [this props] (item-ident props))
  static om/IQuery
  (query [this] [:kind :db/id :thing/label])
  static fc/InitialAppState
  (initial-state [c p] {:db/id :no-selection :kind :thing})
  Object
  (render [this]
    (let [{:keys [db/id thing/label]} (om/props this)]
      (dom/div nil
        (if (= id :no-selection)
          "Nothing selected"
          (str "Details about thing " label))))))

(defui ^:once PersonListItem
  static om/Ident
  (ident [this props] (item-ident props))
  static om/IQuery
  (query [this] [:kind :db/id :person/name])
  Object
  (render [this]
    (let [{:keys [db/id person/name] :as props} (om/props this)
          onSelect (om/get-computed this :onSelect)]
      (dom/li #js {:onClick #(onSelect (item-ident props))}
        (dom/a #js {:href "javascript:void(0)"} (str "Person " id " " name))))))

(def ui-person (om/factory PersonListItem {:keyfn item-key}))

(defui ^:once PlaceListItem
  static om/Ident
  (ident [this props] (item-ident props))
  static om/IQuery
  (query [this] [:kind :db/id :place/name])
  Object
  (render [this]
    (let [{:keys [db/id place/name] :as props} (om/props this)
          onSelect (om/get-computed this :onSelect)]
      (dom/li #js {:onClick #(onSelect (item-ident props))}
        (dom/a #js {:href "javascript:void(0)"} (str "Place " id " : " name))))))

(def ui-place (om/factory PlaceListItem {:keyfn item-key}))

(defui ^:once ThingListItem
  static om/Ident
  (ident [this props] (item-ident props))
  static om/IQuery
  (query [this] [:kind :db/id :thing/label])
  Object
  (render [this]
    (let [{:keys [db/id thing/label] :as props} (om/props this)
          onSelect (om/get-computed this :onSelect)]
      (dom/li #js {:onClick #(onSelect (item-ident props))}
        (dom/a #js {:href "javascript:void(0)"} (str "Thing " id " : " label))))))

(def ui-thing (om/factory ThingListItem item-key))

(defrouter ItemDetail :detail-router
  (ident [this props] (item-ident props))
  :person PersonDetail
  :place PlaceDetail
  :thing ThingDetail)

(def ui-item-detail (om/factory ItemDetail))

(defui ^:once ItemUnion
  static om/Ident
  (ident [this props] (item-ident props))
  static om/IQuery
  (query [this] {:person (om/get-query PersonListItem)
                 :place  (om/get-query PlaceListItem)
                 :thing  (om/get-query ThingListItem)})
  Object
  (render [this]
    (let [{:keys [kind] :as props} (om/props this)]
      (case kind
        :person (ui-person props)
        :place (ui-place props)
        :thing (ui-thing props)))))

(def ui-item-union (om/factory ItemUnion {:keyfn item-key}))

(defui ^:once ItemList
  static fc/InitialAppState
  (initial-state [c p]
    ; These would normally be loaded...but for demo purposes we just hand code a few
    {:items [(make-person 1 "Tony")
             (make-thing 2 "Toaster")
             (make-place 3 "New York")
             (make-person 4 "Sally")
             (make-thing 5 "Pillow")
             (make-place 6 "Canada")]})
  static om/Ident
  (ident [this props] [:lists/by-id :singleton])
  static om/IQuery
  (query [this] [{:items (om/get-query ItemUnion)}])
  Object
  (render [this]
    (let [{:keys [items]} (om/props this)
          onSelect (om/get-computed this :onSelect)]
      (dom/ul nil
        (map (fn [i] (ui-item-union (om/computed i {:onSelect onSelect}))) items)))))

(def ui-item-list (om/factory ItemList))

(defui ^:once DemoRoot
  static om/IQuery
  (query [this] [{:item-list (om/get-query ItemList)}
                 {:item-detail (om/get-query ItemDetail)}])
  static fc/InitialAppState
  (initial-state [c p] (merge
                         (r/routing-tree
                           (r/make-route :detail [(r/router-instruction :detail-router [:param/kind :param/id])]))
                         {:item-list   (fc/get-initial-state ItemList nil)
                          :item-detail (fc/get-initial-state ItemDetail nil)}))
  Object
  (render [this]
    (let [{:keys [item-list item-detail]} (om/props this)
          ; This is the only thing to do: Route the to the detail screen with the given route params!
          showDetail (fn [[kind id]]
                       (om/transact! this `[(r/route-to {:handler :detail :route-params {:kind ~kind :id ~id}})]))]
      ; devcards, embed in iframe so we can use bootstrap css easily
      (ele/ui-iframe {:frameBorder 0 :height "300px" :width "100%"}
        (dom/div #js {:key "example-frame-key"}
          (dom/style nil ".boxed {border: 1px solid black}")
          (dom/link #js {:rel "stylesheet" :href "bootstrap-3.3.7/css/bootstrap.min.css"})
          (b/container-fluid {}
            (b/row {}
              (b/col {:xs 6} "Items")
              (b/col {:xs 6} "Detail"))
            (b/row {}
              (b/col {:xs 6} (ui-item-list (om/computed item-list {:onSelect showDetail})))
              (b/col {:xs 6} (ui-item-detail item-detail)))))))))

