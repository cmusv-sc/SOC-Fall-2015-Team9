/**
 *
 *
 * Created by seckcoder on 11/18/15.
 *
 * For the stupidest course I've ever taken. It's called SOC
 */


$(document).ready(function () {
    $("#comments-container").empty();
    $("#submitBtn").click(function () {
        var htag = $("#inputHashTag")[0].value.substr(1);
        $.ajax({
            url: "/comment/searchCommentByHashTag/" + htag
        }).done(function (comments) {
            console.log(comments);
        }).fail(function (err) {
            console.log(err);
        });
    });
});
