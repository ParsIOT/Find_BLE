
let center = [35.70434, 51.40257],
    grid, map;

function onDocReady() {

    // let layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1Ijoic210YWJhc2kiLCJhIjoiY2o2ZGI2dDFrMjJjaDJxbHNydXNmcGFreSJ9.rxk3Y0HzuvFClOTV_xzSWQ', {
    //     maxZoom: 22,
    //     attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
    //     '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
    //     'Imagery © <a href="http://mapbox.com">Mapbox</a>',
    //     id: 'mapbox.streets'
    // });
    //
    // map = L.map('map_frame', {
    //     layers: [layer],
    //     center: center,
    //     zoom: 18
    // });
    //
    // L.control.scale({imperial: false}).addTo(map);

    map = L.map('map_frame', {
        crs: L.CRS.Simple,
        minZoom: -10
    });

    var bounds = [[-1200, -1800], [1200, 1800]];
//    var bounds = [[-400, -300], [400, 300]];
    var image = L.imageOverlay('img/test-map.png', bounds).addTo(map);


    map.setView([0, 0], -3);
    let latlng = [0, 0];
    // var marker = L.userMarker(latlng, {pulsing: true, accuracy: 100, smallIcon: true});
    // marker.addTo(map);
    //
    // map.on('click', function (e) {
    //     console.log(e.latlng.lat + "," + e.latlng.lng);
    //     var retVar = e.latlng.lat + "," + e.latlng.lng;
    //     Android.sendToAndroid(retVar);
    // });
    // marker.on('click', function marketOnClick(e) {
    //     marker._popup.setContent("<b>x = " + e.latlng.lat + "</b><br><b>y = " + e.latlng.lng + "</b>");
    //     marker.openPopup();
    // });

    grid = new L.GridToolbar(latlng, 2,
        {   mode:'indoor',
            scale: false,
            rotate: false,
            width: 32,
            height: 20}
    ).addTo(map);



    function update_map(arg) {
        console.log(arg);
        var pattern = /(.*),(.*)/;
        var match = pattern.exec(arg);
        console.log(match);
        var x = match[1];
        var y = match[2];
        marker.slideTo([x, y], {
            duration: 1000,
            keepAtCenter: false
        });
    }

    function showLocations(arg) {
        points = arg.split(" ");
        for (var s in points) {

        }
    }

    function changeAccuracy(accuracy) {
        marker.setAccuracy(accuracy);
    }



    //---------------------------------------------- TEST ---------------------------------------------

    // grid = new L.GridToolbarNonGeo([0, 0], 10,
    //     {width: 40, height: 80}).addTo(map);

}

$(document).ready(onDocReady);