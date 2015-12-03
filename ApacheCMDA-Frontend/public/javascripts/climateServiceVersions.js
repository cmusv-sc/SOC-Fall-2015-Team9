/**
 * Created by seckcoder on 12/1/15.
 */

$(document).ready(function () {

    $("#versions").empty();

    var serviceId = $("#serviceId");

    $.ajax({
        url: "/climate/getAllVersionsData/" + serviceId,
    }).done(function (ret) {

    }).fail(function (err) {
        console.log(err);
    });
});