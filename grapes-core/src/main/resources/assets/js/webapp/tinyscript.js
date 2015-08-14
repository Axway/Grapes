/**
 * Created by jennifer on 7/23/15.
 */
$(document).ready(function() {
   var hi = $("#isAdmin").attr("value");
    if(hi === "" || hi.length===0){
        $(document.body).append("not set");
    }
    else{
        $(document.body).append(hi);
    }

    });
