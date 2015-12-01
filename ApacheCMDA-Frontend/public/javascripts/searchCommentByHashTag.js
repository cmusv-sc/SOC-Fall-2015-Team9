/**
 *
 *
 * Created by seckcoder on 11/18/15.
 *
 * For the stupidest course I've ever taken. It's called SOC
 */


jQuery(document).ready(function ($) {
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

    $.ajax({
        url: "/climate/getServicesAutoCompleteSource"
    }).done(function (ret) {
	function split( val ) {
	    return val.split(/@|#\s*/);
	}
	function extractLast(term) {
	    return split(term).pop();
	}
	$('#inputHashTag').autocomplete({
	    source: function(request, response) {
		var re = $.ui.autocomplete.escapeRegex(request.term);
		var last = re.substring(re.lastIndexOf('@'));
		var matcher = new RegExp( "^" + last, "i" );
		var a = $.grep(ret.services, function(item,index){
		    return matcher.test(item);
		});
		response($.ui.autocomplete.filter(a, extractLast(request.term)));
	    },
	    select: function(event, ui) {
		while (this.value[this.value.length - 1] != '@'){
		    this.value = this.value.substring(0, this.value.length - 1);
		}
		this.value = this.value + ui.item.value.substring(1);
		return false;
	    },
	    delay: 0
	}).data('ui-autocomplete')._renderItem =  function (ul, item){
	    var newText = String(item.value).replace(new RegExp(this.term.substring(this.term.lastIndexOf('@')), "gi"),
						     "<span style='font-weight:bold;color:Blue;'>$&</span>");
	    return $("<li>")
		.data("ui-autocomplete-item", item)
		.append("<a>" + newText + "</a>")
		.appendTo(ul);
	};
    }).fail(function (err) {
        console.log(err);
    });
    
    $("#submitBtn").click(function () {
        var htag = $("#inputHashTag")[0].value.substr(1);
        $.ajax({
            url: "/comment/searchCommentByHashTag/" + htag
        }).done(function (ret) {
	    var comments = ret.comments;
            $("#comments-container").empty();

            comments.forEach(function (comment) {
                $("#comments-container").append(commentHTML(comment));
            });
        }).fail(function (err) {
            console.log(err);
        });
    });
});
