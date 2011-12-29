;; C++ ��� �������� �м���(C++ Header Dependency Analyzer)
;;
;; @author: �Կ���(Younghoon Ham)
;; @date: 2011.12.08(Thu)
;;
;; C++ ������Ʈ�� �����ϰ� �ִ� �ҽ� �� ��� ���ϵ� ����
;; ���� ���踦 �м��� ���� .dot �������� ����ϰ�, �̸�
;; graphviz�� �̿��Ͽ� �̹��� ���Ϸ� ��ȯ�����ν�
;; ��� ���ϵ� ���� ���� ���踦 �ð������� Ȯ���� ��
;; �ְ� �Ѵ�.

(ns warmworm.da
  (:import (java.io File))
  (:use [clojure.contrib.duck-streams :only (reader append-spit)]))

;; file�� �����ϰ� �ִ� ��� ������ ����� ǥ���ϴ� ����ü
(defstruct dep-set :file :headers)

(defn cpp-file?
  "file�� C ���� �������� Ȯ���Ѵ�."
  [file]
  (let [filename (.toLowerCase (.toString file))]
    (some #(.endsWith filename %)
          [".h" ".hpp" ".c" ".cpp" ".inl"])))

(defn get-header-name
  "#include <stdio.h> ������ ���ڿ����� ���� �̸��� �����Ѵ�."
  [name]
  (println name) ;; TEST
  (name)) ;; TEST
;;  (#(re-find #"\w+" %) name))

;; ����ǥ������ �̿��ؼ� line���� ��� ���� �̸���
;; ������ ���� coll�� �߰��Ѵ�.
(defn extract-header
  "���Ͽ��� �����ϴ� ���ϵ��� ����� �����Ѵ�."
  [filename rdr]
  (let [headers (filter #(re-find #"#include" %) (line-seq rdr))]
    (if (> (count headers) 0)
      (struct dep-set filename headers)
;;      (struct dep-set filename (map get-header-name headers))
      nil)))
  ;; (struct dep-set filename
  ;;         (map get-header-name
  ;;              (filter #(re-find #"#include" %)
  ;;                      (line-seq rdr)))))

(defn parse-header
  "base-dir �� ���� ���丮�� C++���� ���ϵ��� �м��ؼ� ������ ������ �����ϰ� �ִ� ��� ������ ������ coll�� �����Ѵ�."
  [base-dir]
  (for [file (file-seq (File. base-dir))
        :when (cpp-file? file)]
    (with-open [rdr (reader file)]
      (extract-header (.toString file) rdr))))

(defn make-dot-file
  "�������� ��� �������� ������ .dot ������ ���Ϸ� ��������."
  [filename dep]
  (when (> (count dep) 0)
    (spit filename "digraph dep_diagram {\n")
    (for [s dep]
      (doseq [lookat (:headers s)]
        (append-spit filename (str (:file s) " -> " lookat "\n"))))
    (append-spit filename "}\n")))

(defn make-img-file
  ".dot ������ ������ �̹��� ���Ϸ� ��ȯ�Ѵ�."
  [dot-name img-name img-ext]
  (let [arg (str "dot -T" img-ext " " dot-name " -o " img-name)]
    (.. Runtime getRuntime (exec arg))))

;;============================================================
;; ��� ������ ���� ���踦 �м��Ѵ�.
;;============================================================

(defn main
  "���α׷� ��Ʈ�� ����Ʈ"
  []
  (println "wait...")
  (let [img-type "png" ; ����� �̹��� ���� Ÿ��
        img-name (str "output." img-type) ; ����� �̹��� �̸�
        dot-name "output.dot"] ; ����� ���� �̸�
    (make-dot-file dot-name (parse-header "."))
    ;;(make-img-file dot-name img-name img-type))
  (println "done.")))

(main)

