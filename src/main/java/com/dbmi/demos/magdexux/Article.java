package com.dbmi.demos.magdexux;

public class Article {
        protected long     articleId;
        protected String articleTitle;
        protected String articleAuthor;
        protected String articleSynopsis;
        protected String  articleCategory;
        protected String articleKeywords;
        protected int    articleMonth;
        protected int    articleYear;

        public Article() {}

        public Article(long id, String title, String synopsis, String category, String keywords,
                       int month, int year) {
            this.articleId          = id;
            this.articleTitle       = title;
            this.articleSynopsis    = synopsis;
            this.articleCategory    = category;
            this.articleKeywords     = keywords;
            this.articleMonth       = month;
            this.articleYear        = year;
        } // CONSTRUCTOR(INT,STRING,STRING)

        public long getArticleId() {
            return articleId;
        } // GETARTICLEID()

        public void setArticleId(long articleId) {
            this.articleId = articleId;
        }

        public String getArticleTitle() {
            return articleTitle;
        }

        public void setArticleTitle(String articleTitle) {
            this.articleTitle = articleTitle;
        }

        public String getArticleSynopsis() {
            return articleSynopsis;
        }

        public void setArticleSynopsis(String articleSynopsis) {
            this.articleSynopsis = articleSynopsis;
        }

        public String getArticleCategory() {
            return articleCategory;
        }

        public void setArticleCategory(String articleCategory) {
            this.articleCategory = articleCategory;
        }

        public String getArticleKeywords() {
            return articleKeywords;
        }

        public void setArticleKeywords(String articleKeywords) {
            this.articleKeywords = articleKeywords;
        }

        public int getArticleMonth() {
            return articleMonth;
        }

        public void setArticleMonth(int articleMonth) {
            this.articleMonth = articleMonth;
        }

        public int getArticleYear() {
            return articleYear;
        }

        public void setArticleYear(int articleYear) {
            this.articleYear = articleYear;
        }
        public String getArticleAuthor() {
            return articleAuthor;
        }

        public void setArticleAuthor(String articleAuthor) {
            this.articleAuthor = articleAuthor;
        }
} // CLASS
