;; C++ 헤더 의존관계 분석기(C++ Header Dependency Analyzer)
;;
;; @author: 함영훈(Younghoon Ham)
;; @date: 2011.12.08(Thu)
;;
;; C++ 프로젝트를 구성하고 있는 소스 및 헤더 파일들 간의
;; 의존 관계를 분석한 다음 .dot 포맷으로 출력하고, 이를
;; graphviz를 이용하여 이미지 파일로 변환함으로써
;; 헤더 파일들 간의 의존 관계를 시각적으로 확인할 수
;; 있게 한다.

(ns chda
  (:import (java.io File))
  (:use [clojure.contrib.duck-streams :only (reader append-spit)]))

;; file이 의존하고 있는 헤더 파일의 목록을 표현하는 구조체
(defstruct dep-set :file :headers)

(defn cpp-file?
  "file이 C 관련 파일인지 확인한다."
  [file]
  (let [filename (.toLowerCase (.toString file))]
    (some #(.endsWith filename %)
          [".h" ".hpp" ".c" ".cpp" ".inl"])))

(defn get-header-name
  "#include <stdio.h> 형태의 문자열에서 파일 이름을 추출한다."
  [name]
  (println name) ;; TEST
  (name)) ;; TEST
;;  (#(re-find #"\w+" %) name))

;; 정규표현식을 이용해서 line에서 헤더 파일 이름을
;; 추출한 다음 coll에 추가한다.
(defn extract-header
  "파일에서 의존하는 파일들의 목록을 추출한다."
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
  "base-dir 및 하위 디렉토리의 C++관련 파일들을 분석해서 각각의 파일이 포함하고 있는 헤더 파일을 정보를 coll에 저장한다."
  [base-dir]
  (for [file (file-seq (File. base-dir))
        :when (cpp-file? file)]
    (with-open [rdr (reader file)]
      (extract-header (.toString file) rdr))))

(defn make-dot-file
  "보관중인 헤더 의존관계 정보를 .dot 형식의 파일로 내보낸다."
  [filename dep]
  (when (> (count dep) 0)
    (spit filename "digraph dep_diagram {\n")
    (for [s dep]
      (doseq [lookat (:headers s)]
        (append-spit filename (str (:file s) " -> " lookat "\n"))))
    (append-spit filename "}\n")))

(defn make-img-file
  ".dot 형식의 파일을 이미지 파일로 변환한다."
  [dot-name img-name img-ext]
  (let [arg (str "dot -T" img-ext " " dot-name " -o " img-name)]
    (.. Runtime getRuntime (exec arg))))

;;============================================================
;; 헤더 파일의 의존 관계를 분석한다.
;;============================================================

(defn main
  "프로그램 엔트리 포인트"
  []
  (println "wait...")
  (let [img-type "png" ; 출력할 이미지 파일 타입
        img-name (str "output." img-type) ; 출력할 이미지 이름
        dot-name "output.dot"] ; 출력할 파일 이름
    (make-dot-file dot-name (parse-header "."))
    (make-img-file dot-name img-name img-type))
  (println "done.")))

(main)

