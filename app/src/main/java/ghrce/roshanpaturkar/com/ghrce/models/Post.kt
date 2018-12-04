package ghrce.roshanpaturkar.com.ghrce.models

class Post() {

    var name: String? = null
    var userId: String? = null
    var caption: String? = null
    var description: String? = null
    var image: String? = null
    var like: Int? = null
    var dislike: Int? = null
    var comment: Int? = null

    constructor(name: String, userId: String, caption: String, description: String, image: String, like: Int, dislike: Int, comment:Int): this(){
        this.name = name
        this.userId = userId
        this.caption = caption
        this.description = description
        this.image = image
        this.like = like
        this.dislike = dislike
        this.comment = comment
    }

}