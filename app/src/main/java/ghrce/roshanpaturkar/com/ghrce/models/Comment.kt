package ghrce.roshanpaturkar.com.ghrce.models

class Comment() {
    var name: String? = null
    var comment: String? = null

    constructor(name: String, comment: String): this() {
        this.name = name
        this.comment = comment
    }
}