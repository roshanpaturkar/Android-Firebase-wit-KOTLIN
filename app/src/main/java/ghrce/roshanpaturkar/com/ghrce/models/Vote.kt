package ghrce.roshanpaturkar.com.ghrce.models

class Vote() {
    var name: String? = null
    var issue: String? = null
    var image: String? = null
    var agree: Int? = null
    var disAgree: Int? = null

    constructor(name: String, issue: String, image: String, agree: Int, disAgree: Int): this(){
        this.name = name
        this.issue = issue
        this.image = image
        this.agree = agree
        this.disAgree = disAgree
    }
}