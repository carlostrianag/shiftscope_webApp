
/**
 * Load page into url
 *
 * @param url           The url to load
 */
function loadPage(url) {
    var xmlhttp = new XMLHttpRequest(url);

    // Callback function when XMLHttpRequest is ready
    xmlhttp.onreadystatechange = function(){
        if (xmlhttp.readyState === 4){
            if (xmlhttp.status === 200) {
                Debugger.display("joder");
            }
        }
    };
    xmlhttp.open("GET", url , false);
    xmlhttp.send();
}

function loadViewPage(url) {
    Debugger.open(url);
}
