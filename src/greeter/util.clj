(ns hello.util)


;; Utility functions to translate database return values
(defn strip-keywords-from-map
  "Return a new map such that keyword keys are replaced with string keys"
  [m]
  (let [{:keys [forename surname]} m]
    { "forename" forename "surname" surname }))

(defn strip-keywords-from-array
  "Takes in [{:forename \"surname\"} {:forename2 \"surname2\"}] and gives back [{\"forename\" \"surname\"} {\"forename2\" \"surname2\"}]"
  [array-of-maps]
  (map strip-keywords-from-map array-of-maps))
  
