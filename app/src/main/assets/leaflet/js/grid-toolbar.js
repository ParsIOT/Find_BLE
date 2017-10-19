L.GridToolbar = function (center, length, options) {

    this.constant = {
        mode: {
            GEO: 'geo',
            INDOOR: 'indoor'
        },
        tool: {
            MOVE: 'move',
            SCALE: 'scale',
            ROTATE: 'rotate'
        }
    };

    this.options = {

        mode: this.constant.mode.GEO,

        move: true,
        scale: true,
        rotate: true,

        // grid options
        hParts: null,   // number of divisions
        vParts: null,
        width: null,    // in meters, used if hParts/vParts are null
        height: null,

        // line style
        color: '#e00e50',
        weight: 2,
        opacity: 1,
        dashArray: [8, 8],

        // toolbar
        position: 'topright',
        showIcon: 'fa-th',
        hideIcon: 'fa-th',
        eMoveIcon: 'fa-arrows',
        dMoveIcon: 'fa-arrows',
        eScaleIcon: 'fa-arrows-alt',
        dScaleIcon: 'fa-arrows-alt',
        eRotateIcon: 'fa-rotate-right',
        dRotateIcon: 'fa-rotate-right',

        // slider options
        sync: true,
        minScale: 2,
        maxScale: 20,
        minAngle: -45,
        maxAngle: 45,
        sliderSize: 200
    };

    for (let key in options) {
        if (options.hasOwnProperty(key) && options[key] != null) {
            this.options[key] = options[key];
        }
    }

    this.map = null;
    this.center = center;

    // make sure init length is between min and max of slider
    if (length && length >= this.options.minScale && length <= this.options.maxScale)
        this.length = length;
    else
        this.length = 5;

    this.angle = 0;

    this.rotateMatrix = L.matrix(1, 0, 0, 1, 0, 0);
    this.moveMatrix = L.matrix(1, 0, 0, 1, 0, 0);

    this.showGridButton = this.makeMainButton();
    let tools = [this.showGridButton,];
    if (this.options.move) {
        this.moveGridButton = this.makeMoveButton();
        tools.push(this.moveGridButton)
    }
    if (this.options.scale) {
        this.scaleGridButton = this.makeScaleButton();
        this.scaleGridSlider = this.makeScaleSlider();
        tools.push(this.scaleGridButton)
    }
    if (this.options.rotate) {
        this.rotateGridButton = this.makeRotateButton();
        this.rotateGridSlider = this.makeRotateSlider();
        tools.push(this.rotateGridButton)
    }
    this.toolbarElements = null;
    this.toolbar = L.easyBar(tools,
        {position: this.options.position});

    this.gridGroup = this.drawGrid();

    this.dragRect = L.rectangle(this.gridGroup.getBounds(),
        {
            draggable: true,
            color: this.options.color,
            fillColor: this.options.color,
            weight: 2,
            opacity: 0.5,
            fillOpacity: 0.2,
            lineCap: 'butt',
            dashArray: this.options.dashArray
        }
    );

};

L.GridToolbar.prototype = {
    constructor: L.GridToolbar,

    makeMainButton: function () {

        return L.easyButton({
            states: [{
                stateName: 'show',
                icon: this.options.showIcon,
                title: 'show-grid',
                tagName: 'grid-button-bar',
                onClick: function (btn, map) {

                    this.gridGroup.addTo(map);
                    // this.gridGroup.eachLayer(function (line) {
                    //     line.transform.disable();
                    // });

                    if (this.options.move) this.moveGridButton.state('enable');
                    if (this.options.scale) this.scaleGridButton.state('enable');
                    if (this.options.rotate) this.rotateGridButton.state('enable');
                    this.toolbarElements.appendTo("div.easy-button-container").fadeIn();
                    this.toolbarElements = null;

                    btn.state('hide');
                }.bind(this)
            }, {
                stateName: 'hide',
                icon: this.options.hideIcon,
                title: 'hide-grid',
                tagName: 'grid-button-bar',
                onClick: function (btn, map) {

                    if (this.options.move) this.dragRect.remove();
                    this.gridGroup.remove();

                    if (this.options.scale) this.scaleGridSlider.remove();
                    if (this.options.rotate) this.rotateGridSlider.remove();
                    this.toolbarElements = $('button[title="move-grid"], button[title="scale-grid"], button[title="rotate-grid"]').hide().detach();

                    btn.state('show');
                }.bind(this)
            }]
        });
    },


    makeScaleButton: function () {
        return L.easyButton({
            states: [{
                stateName: 'enable',
                icon: this.options.eScaleIcon,
                title: 'scale-grid',
                tagName: 'grid-button-bar',
                onClick: function (btn, map) {

                    if (this.options.move) {
                        this.dragRect.remove();
                        this.moveGridButton.state('enable');
                    }
                    if (this.options.rotate) {
                        this.rotateGridSlider.remove();
                        this.rotateGridButton.state('enable');
                    }
                    this.scaleGridSlider.options.value = this.length;
                    this.scaleGridSlider.addTo(map);
                    $('.leaflet-control-slider').addClass('leaflet-bar');
                    btn.state('disable');
                }.bind(this)
            }, {
                stateName: 'disable',
                icon: this.options.dScaleIcon,
                title: 'scale-grid',
                tagName: 'grid-button-bar',
                onClick: function (btn, map) {
                    if (this.options.rotate) this.rotateGridSlider.remove();
                    this.scaleGridSlider.remove();
                    btn.state('enable');
                }.bind(this)
            }]
        });
    },

    makeRotateButton: function () {
        return L.easyButton({
            states: [{
                stateName: 'enable',
                icon: this.options.eRotateIcon,
                title: 'rotate-grid',
                tagName: 'grid-button-bar',
                onClick: function (btn, map) {
                    if (this.options.move) {
                        this.dragRect.remove();
                        this.moveGridButton.state('enable');
                    }
                    if (this.options.scale) {
                        this.scaleGridSlider.remove();
                        this.scaleGridButton.state('enable');
                    }
                    this.rotateGridSlider.options.value = this.angle;
                    this.rotateGridSlider.addTo(map);
                    $('.leaflet-control-slider').addClass('leaflet-bar');
                    btn.state('disable');
                }.bind(this)
            }, {
                stateName: 'disable',
                icon: this.options.dRotateIcon,
                title: 'rotate-grid',
                tagName: 'grid-button-bar',
                onClick: function (btn, map) {
                    this.rotateGridSlider.remove();
                    btn.state('enable');
                }.bind(this)
            }]
        });
    },


    makeMoveButton: function () {
        return L.easyButton({
            states: [{
                stateName: 'enable',
                icon: this.options.eMoveIcon,
                title: 'move-grid',
                tagName: 'grid-button-bar',
                onClick: function (btn, map) {

                    if (this.options.scale) {
                        this.scaleGridSlider.remove();
                        this.scaleGridButton.state('enable');
                    }
                    if (this.options.rotate) {
                        this.rotateGridSlider.remove();
                        this.rotateGridButton.state('enable');
                    }
                    this.dragRect = L.rectangle(this.gridGroup.getBounds(),
                        {
                            draggable: true,
                            color: this.options.color,
                            fillColor: this.options.color,
                            weight: 2,
                            opacity: 0.5,
                            fillOpacity: 0.2,
                            lineCap: 'butt',
                            dashArray: this.options.dashArray
                        }
                    ).addTo(map);

                    this.dragRect.dragging.enable();

                    this.dragRect
                        .on('drag', function (e) {

                            this.gridGroup.eachLayer(function (line) {
                                this.moveMatrix = this.dragRect.dragging._matrix;
                                line._transform(this.moveMatrix);
                            }.bind(this))

                        }.bind(this))

                        .on('dragend', function (e) {

                            this.gridGroup.eachLayer(function (line) {
                                line.dragging._transformPoints(this.moveMatrix);
                                line._updatePath();
                                line._project();
                                line._transform(null);
                            }.bind(this));

                            let bounds = this.gridGroup.getBounds();
                            this.center = [bounds.getCenter().lat, bounds.getCenter().lng];

                        }.bind(this));

                    btn.state('disable');
                }.bind(this)
            }, {
                stateName: 'disable',
                icon: this.options.dMoveIcon,
                title: 'move-grid',
                tagName: 'grid-button-bar',
                onClick: function (btn, map) {
                    this.dragRect.remove();
                    btn.state('enable');
                }.bind(this)
            }]
        });
    },


    makeRotateSlider: function () {

        return L.control.slider(function (value) {

                this.angle = value;
                let radians = value * Math.PI / 180;
                // this.rotateMatrix = L.Matrix([Math.cos(radians), -Math.sin(radians),
                //     Math.sin(radians), Math.cos(radians), 0, 0]);
                this.gridGroup.removeFrom(this.map);
                this.gridGroup = this.drawGrid();
                this.gridGroup.addTo(this.map);

                this.gridGroup.eachLayer(function (line) {
                    // console.log(line._map);
                    line._map = this.map;
                    line.transform._transformPoints(line, radians, null, new L.latLng(this.center[0], this.center[1]));
                    line._updatePath();
                    line._project();
                    line._transform(null);
                }.bind(this));

                // grid.transform.enable();
            }.bind(this),
            {
                id: 'rotate-slider',
                width: 100,
                min: this.options.minAngle,
                max: this.options.maxAngle,
                value: 0,
                step: 1,
                size: '250px',
                position: this.options.position,
                orientation: 'vertical',
                collapsed: false,
                syncSlider: this.options.sync
            });
    },


    makeScaleSlider: function () {

        return L.control.slider(function (value) {
                this.gridGroup.removeFrom(this.map);
                // delete this.gridGroup;
                this.length = value;
                this.gridGroup = this.drawGrid();
                this.gridGroup.addTo(map);

                this.gridGroup.eachLayer(function (line) {
                    // console.log(line._map);
                    line._map = this.map;
                    line.transform._transformPoints(line, this.angle * Math.PI / 180, null, new L.latLng(this.center[0], this.center[1]));
                    line._updatePath();
                    line._project();
                    line._transform(null);
                }.bind(this));

            }.bind(this),
            {
                id: 'scale-slider',
                value: this.length,
                min: this.options.minScale,
                max: this.options.maxScale,
                step: 0.5,
                size: '250px',
                position: this.options.position,
                orientation: 'vertical',
                collapsed: false,
                syncSlider: this.options.sync
            });
    },

    /**
     * not too accurate!
     */

    metersToLatLng: function (meter, atLat) {
        return [meter / 111111, meter / (111111 * Math.cos(atLat * (Math.PI / 180)))];
    },


    drawLine: function (startX, startY, endX, endY, dash) {
        let line = new L.polyline([[startX, startY], [endX, endY]],
            {
                color: this.options.color,
                weight: this.options.weight,
                opacity: this.options.opacity,
                dashArray: dash,
                lineCap: 'butt',    // could be 'round' if u like!
                transform: true,
                draggable: true
            });

        line.dragging.disable();
        line.transform.disable();
        return line;
    },


    drawGrid: function () {

        let length, xn, yn;

        let center = this.center;

        switch (this.options.mode) {
            case 'geo':
                length = this.metersToLatLng(this.length, center[0]);
                if (this.options.vParts && this.options.hParts) {
                    xn = this.options.hParts / 2 + 1;
                    yn = this.options.vParts / 2 + 1;
                } else if (this.options.width && this.options.height) {
                    xn = ((this.options.width / this.length) / 2 ) + 1;
                    yn = ((this.options.height / this.length) / 2) + 1;
                } else {
                    // console.log(this.options.vParts, this.options.hParts);
                    // console.log(this.options.width, this.options.height);
                    xn = 9;
                    yn = 9;
                }
                break;
            case 'indoor':
                length = [this.length * 100, this.length * 100];
                if (this.options.vParts && this.options.hParts) {
                    yn = this.options.hParts / 2 + 1;
                    xn = this.options.vParts / 2 + 1;
                } else if (this.options.width && this.options.height) {
                    yn = ((this.options.width / this.length) / 2 ) + 1;
                    xn = ((this.options.height / this.length) / 2) + 1;
                } else {
                    // console.log(this.options.vParts, this.options.hParts);
                    // console.log(this.options.width, this.options.height);
                    xn = 9;
                    yn = 9;
                }
                break;
        }


        let gridArray = [];

        let minX = center[0] - (xn * length[0]),
            maxX = center[0] + (xn * length[0]),
            minY = center[1] - (yn * length[1]),
            maxY = center[1] + (yn * length[1]);

        // gridGroup.push(drawLine(minX, minY, maxX, maxY, [1]));
        // gridGroup.push(drawLine(maxX, minY, minX, maxY, [1]));

        gridArray.push(this.drawLine(center[0], minY, center[0], maxY));
        gridArray.push(this.drawLine(minX, center[1], maxX, center[1]));

        for (let n = 1; n < xn; n++) {
            if (n % 2 == 0) {
                gridArray.push(this.drawLine(center[0] + n * length[0], minY, center[0] + n * length[0], maxY));
                gridArray.push(this.drawLine(center[0] - n * length[0], minY, center[0] - n * length[0], maxY));
            } else {
                gridArray.push(this.drawLine(center[0] + n * length[0], minY, center[0] + n * length[0], maxY, this.options.dashArray));
                gridArray.push(this.drawLine(center[0] - n * length[0], minY, center[0] - n * length[0], maxY, this.options.dashArray));
            }
        }

        for (let n = 1; n < yn; n++) {
            if (n % 2 == 0) {
                gridArray.push(this.drawLine(minX, center[1] + n * length[1], maxX, center[1] + n * length[1]));
                gridArray.push(this.drawLine(minX, center[1] - n * length[1], maxX, center[1] - n * length[1]));
            } else {
                gridArray.push(this.drawLine(minX, center[1] + n * length[1], maxX, center[1] + n * length[1], this.options.dashArray));
                gridArray.push(this.drawLine(minX, center[1] - n * length[1], maxX, center[1] - n * length[1], this.options.dashArray));
            }
        }
        return L.featureGroup(gridArray);
    },


    addTo: function (map) {
        this.map = map;
        // console.log(map);
        this.toolbar.addTo(map);
        this.toolbarElements = $('button[title="move-grid"], button[title="scale-grid"], button[title="rotate-grid"]').hide().detach();
        return this;
    }

};