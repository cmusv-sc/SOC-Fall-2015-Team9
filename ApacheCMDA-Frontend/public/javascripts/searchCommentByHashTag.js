/**
 *
 *
 * Created by seckcoder on 11/18/15.
 *
 * For the stupidest course I've ever taken. It's called SOC
 */


$(document).ready(function () {
    var commentHTML= function (comment) {
        return "<div class='posted-comment-container clearfix'>" +
                    "<div class='avatar pull-left'>" +
                        "<div class=>" +
                            "<img class='ui-corner-all curr-user-photo' border='0' src='/assets/images/user_blank_picture.png'></img>" +
                        "</div>" +
                    "</div>" +
                    "<div class='posted-comment-sub-container pull-right'>" +
                        "<div class='posted-comment-head clearfix'>" +
                            "<span class='posted-comment-author pull-left'>" + comment.fullname + "</span>" +
                            "<span class='posted-comment-date pull-right'>" + comment.posted_date + "</span>" +
                        "</div>" +
                        "<div class='posted-comment-body'>" +
                            "<div class='posted-comment-text'>" + comment.text + "</div>" +
                        "</div>" +
                    "</div>"
                "</div>";
    };
    $("#comments-container").empty();
    $("#submitBtn").click(function () {
        var htag = $("#inputHashTag")[0].value.substr(1);
        $.ajax({
            url: "/comment/searchCommentByHashTag/" + htag
        }).done(function (ret) {
            var comments = ret.comments;
            console.log(comments);
            $("#comments-container").empty();

            comments.forEach(function (comment) {
                $("#comments-container").append(commentHTML(comment));
            });
        }).fail(function (err) {
            console.log(err);
        });
    });
});
