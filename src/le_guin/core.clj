(ns le-guin.core
  (:require [clj-vxe.core :refer :all]
            [yaml.core :as yaml])
  (:use [me.raynes.conch.low-level :as sh]))

(def default-playbook [#ordered/map
                       ([:name "USE-method"]
                        [:hosts "custom"]
                        [:tags ["custom"]]
                        [:tasks [#ordered/map
                                 ([:name "uptime - number of tasks (processes) wanting to run"]
                                  [:shell "uptime"]
                                  [:register "uptime"])
                                 #ordered/map
                                 ([:debug "var=uptime.stdout_lines"])
                                 #ordered/map
                                 ([:name "dmesg -T | grep -i error | grep -v usb - OS logging"]
                                  [:shell "dmesg -T | grep -i error | grep -v usb"] [:register "dmesg"])
                                 #ordered/map
                                 ([:name "free - available memory"]
                                  [:shell "free -mh"]
                                  [:register "free"])
                                 #ordered/map
                                 ([:debug "var=free.stdout_lines"])
                                 #ordered/map
                                 ([:name "vmstat 1 1 - memory and cpu behavior statistics"]
                                  [:shell "vmstat 1 1"]
                                  [:register "vmstat"])
                                 #ordered/map
                                 ([:debug "var=vmstat.stdout_lines"])
                                 #ordered/map
                                 ([:name "mpstat -P ALL 1 1 - cpu statistics per core"]
                                  [:shell "mpstat -P ALL 1 1"]
                                  [:register "mpstat"])
                                 #ordered/map
                                 ([:debug "var=mpstat.stdout_lines"])
                                 #ordered/map
                                 ([:name "pidstat 1 5 - per process cpu utilization"]
                                  [:shell "pidstat 1 1"]
                                  [:register "pidstat"])
                                 #ordered/map
                                 ([:debug "var=pidstat.stdout_lines"])
                                 #ordered/map
                                 ([:name "iostat -xz 1 3 - disk utilization and queuing"]
                                  [:shell "iostat -xz 1 1"]
                                  [:register "iostat"])
                                 #ordered/map
                                 ([:debug "var=iostat.stdout_lines"])
                                 #ordered/map
                                 ([:name "sudo lsof -iTCP -sTCP:LISTEN - list of processes doing network IO"]
                                  [:shell "sudo lsof -iTCP -sTCP:LISTEN"]
                                  [:register "lsof"])
                                 #ordered/map
                                 ([:debug "var=lsof.stdout_lines"])
                                 #ordered/map
                                 ([:name "sar -n DEV 1 3 - network interface throughput"]
                                  [:shell "sar -n DEV 1 1"]
                                  [:register "sar_throughput"])
                                 #ordered/map
                                 ([:debug "var=sar_throughput.stdout_lines"])
                                 #ordered/map
                                 ([:name "sar -n TCP,ETCP 1 - TCP metrics"]
                                  [:shell "sar -n TCP,ETCP 1 1"]
                                  [:register "sar_tcp_stats"])
                                 #ordered/map
                                 ([:debug "var=sar_tcp_stats.stdout_lines"])
                                 #ordered/map
                                 ([:name "top - system overview for sanity checking"]
                                  [:shell "top -b -n 1 | head -30"]
                                  [:register "top"])
                                 #ordered/map
                                 ([:debug "var=top.stdout_lines"])]])])

(defn generate-inventory
  ([]
   (let [host-list (do (println "enter a comma seperated list of hosts: ")
                       (clojure.core/flush) (clojure.core/read-line))
         hosts (clojure.string/split host-list #",")]

     (spit "hosts" "[custom]\n")
     (doseq [host hosts]
       (spit "hosts" (str host "\n") :append true))))
  ([hosts]
   (spit "hosts" "[custom]\n")
   (doseq [host hosts]
     (spit "hosts" (str host "\n") :append true))))

(defn -main
  "generate playbook"
  []
  (spit "use-method.yml" (yaml/generate-string default-playbook :dumper-options {:flow-style :block}))
  (println "run (generate-inventory) to begin diagnosing hosts"))

(defn le-guin
  ([]
   (sh/stream-to-out (sh/proc "ansible-playbook" "-i" "hosts" "use-method.yml") :out))
  ([playbook]
   (sh/stream-to-out (sh/proc "ansible-playbook" "-i" "hosts" (str "./" playbook)) :out)))

(defn use-method
  ([]
   (sh/stream-to-out (sh/proc "ansible-playbook" "-i" "hosts" "use-method.yml") :out))
  ([hosts]
   (generate-inventory hosts)
   (sh/stream-to-out (sh/proc "ansible-playbook" "-i" "hosts" "use-method.yml") :out)))
