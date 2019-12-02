function drawLayer02Label(canvasObj, text, textBeginX, lineEndX) {
    var colorValue = '#04918B';
    var ctx = canvasObj.getContext("2d");
    ctx.beginPath();
    ctx.arc(35, 55, 2, 0, 2 * Math.PI);
    ctx.closePath();
    ctx.fillStyle = colorValue;
    ctx.fill();
    ctx.moveTo(35, 55);
    ctx.lineTo(60, 80);
    ctx.lineTo(lineEndX, 80);
    ctx.lineWidth = 1;
    ctx.strokeStyle = colorValue;
    ctx.stroke();
    ctx.font = '12px Georgia';
    ctx.fillStyle = colorValue;
    ctx.fillText(text, textBeginX, 92);
}

var COLOR = {
    MACHINE: {
        TYPE_A: '#0175EE',
        TYPE_B: '#D89446',
        TYPE_C: '#373693',
        TYPE_D: '#25AE4F',
        TYPE_E: '#06B5C6',
        TYPE_F: '#009E9A',
        TYPE_G: '#AC266F'
    }
};

function renderLegend() {
    drawLegend(COLOR.MACHINE.TYPE_A, 25, '大索引');
    drawLegend(COLOR.MACHINE.TYPE_B, 50, '小索引');
    drawLegend(COLOR.MACHINE.TYPE_C, 75, '用户信息索引');
    drawLegend(COLOR.MACHINE.TYPE_D, 100, 'ZDR索引');
    drawLegend(COLOR.MACHINE.TYPE_E, 125, '其它索引');
    drawLegend(COLOR.MACHINE.TYPE_F, 150, '专题索引');
    drawLegend(COLOR.MACHINE.TYPE_G, 175, '预警索引');
    drawLegend(COLOR.MACHINE.TYPE_G, 200, '预处理索引');
}

function drawLegend(pointColor, pointY, text) {
    var ctx = $("#layer03_left_01 canvas").get(0).getContext("2d");
    ctx.beginPath();
    ctx.arc(20, pointY, 6, 0, 2 * Math.PI);
    ctx.fillStyle = pointColor;
    ctx.fill();
    ctx.font = '20px';
    ctx.fillStyle = '#FEFFFE';
    ctx.fillText(text, 40, pointY + 3);
}

function renderLayer03Right() {

    var address = sessionStorage.getItem("esClusterAddress");
    var obj = {};
    obj.address = address;
    var json = JSON.stringify(obj);
    $("#loading").show();
    $.ajax({
        type: 'POST',
        async: true,
        url: 'cluster-average-basis-info',
        data: json,
        contentType: "application/json",
        success: function (data) {
            var result = JSON.parse(data.result);
            // 刷新圆形占比数据
            drawLayer03Right($("#layer03_right_chart01 canvas").get(0), "#027825", result.cpu);
            drawLayer03Right($("#layer03_right_chart02 canvas").get(0), "#006DD6", result.heap);
            drawLayer03Right($("#layer03_right_chart03 canvas").get(0), "#238681", result.disk);
        },
        dataType: 'json'
    });
}

function drawLayer03Right(canvasObj, colorValue, rate) {
    var ctx = canvasObj.getContext("2d");
    var circle = {x: 65, y: 80, r: 60};
    ctx.beginPath();
    ctx.arc(circle.x, circle.y, circle.r, 0, Math.PI * 2)
    ctx.lineWidth = 10;
    ctx.strokeStyle = '#052639';
    ctx.stroke();
    ctx.closePath();
    ctx.beginPath();
    ctx.arc(circle.x, circle.y, circle.r, 1.5 * Math.PI, (1.5 + rate * 2) * Math.PI)
    ctx.lineWidth = 10;
    ctx.lineCap = 'round';
    ctx.strokeStyle = colorValue;
    ctx.stroke();
    ctx.closePath();
    ctx.fillStyle = 'white';
    ctx.font = '20px Calibri';
    ctx.fillText(rate * 100 + '%', circle.x - 15, circle.y + 10);
}

function renderChartBar01() {

    var address = sessionStorage.getItem("esClusterAddress");
    var obj = {};
    obj.address = address;
    var json = JSON.stringify(obj);
    $("#loading").show();
    $.ajax({
        type: 'POST',
        async: true,
        url: 'cluster-pie-statistics-info',
        data: json,
        contentType: "application/json",
        success: function (data) {
            var result = JSON.parse(data.result);
            // PIE STATISTICS
            var myChart = echarts.init(document.getElementById("layer03_left_02"));
            myChart.setOption({
                title: {text: '', subtext: '', x: 'center'},
                tooltip: {trigger: 'item', formatter: "{b} : {c} ({d}%)"},
                legend: {
                    show: false,
                    x: 'center',
                    y: 'bottom',
                    data: ['大索引', '小索引', '用户信息索引', 'ZDR索引', '其它索引', '专题索引', '预警索引','预处理索引']
                },
                toolbox: {},
                label: {normal: {show: true, formatter: "{b} \n{d}%"}},
                calculable: true,
                color: [COLOR.MACHINE.TYPE_A, COLOR.MACHINE.TYPE_B, COLOR.MACHINE.TYPE_C, COLOR.MACHINE.TYPE_D, COLOR.MACHINE.TYPE_E, COLOR.MACHINE.TYPE_F, COLOR.MACHINE.TYPE_G],
                series: [{
                    name: '',
                    type: 'pie',
                    radius: [40, 80],
                    center: ['50%', '50%'],
                    data: [{value: result.all, name: '大索引'}, {value: result.small, name: '小索引'}, {value: result.user, name: '用户信息索引'}, {
                        value: result.zdr, name: 'ZDR索引'
                    }, {value: result.other, name: '其它索引'}, {value: result.event, name: '专题索引'}, {value: result.monitor, name: '预警索引'},
                        {value: result.preprocess, name: '预处理索引'}]
                }]
            });
        },
        dataType: 'json'
    });
}

function renderLayer04Left() {
    var myChart = echarts.init(document.getElementById("layer04_left_chart"));
    myChart.setOption({
        title: {text: ''},
        tooltip: {trigger: 'axis'},
        legend: {data: []},
        grid: {left: '3%', right: '4%', bottom: '5%', top: '4%', containLabel: true},
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: getLatestDays(31),
            axisLabel: {textStyle: {color: "white", fontSize: 8}, rotate: 45, interval: 2},
            axisTick: {show: false},
            axisLine: {show: true, lineStyle: {color: '#0B3148', width: 1, type: 'solid'}}
        },
        yAxis: {
            type: 'value',
            axisTick: {show: false},
            axisLabel: {textStyle: {color: "white", fontSize: 8}},
            axisLine: {show: true, lineStyle: {color: '#0B3148', width: 1, type: 'solid'}},
            splitLine: {show: false}
        },
        tooltip: {formatter: '{c}', backgroundColor: '#FE8501'},
        series: [{
            name: '',
            type: 'line',
            smooth: true,
            areaStyle: {
                normal: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                        offset: 0,
                        color: '#026B6F'
                    }, {offset: 1, color: '#012138'}], false), opacity: 0.2
                }
            },
            itemStyle: {normal: {color: '#009991'}, lineStyle: {normal: {color: '#009895', opacity: 1}}},
            symbol: 'none',
            data: [48, 52, 45, 46, 89, 120, 110, 100, 88, 96, 88, 45, 78, 67, 89, 103, 104, 56, 45, 104, 112, 132, 120, 110, 89, 95, 90, 89, 102, 110, 110]
        }]
    });
}

function renderLayer04Right() {
    var myChart = echarts.init(document.getElementById("layer04_right_chart"));
    myChart.setOption({
        title: {text: ''},
        tooltip: {trigger: 'axis'},
        legend: {
            top: 20,
            right: 5,
            textStyle: {color: 'white'},
            orient: 'vertical',
            data: [{name: '网络', icon: 'circle'}, {name: '内存', icon: 'circle'}, {name: 'CPU', icon: 'circle'}]
        },
        grid: {left: '3%', right: '16%', bottom: '3%', top: '3%', containLabel: true},
        xAxis: {
            type: 'category',
            boundaryGap: false,
            axisTick: {show: false},
            axisLabel: {textStyle: {color: "white", fontSize: 8}},
            axisLine: {show: true, lineStyle: {color: '#0B3148', width: 1, type: 'solid'}},
            data: get10MinutesScale()
        },
        yAxis: {
            type: 'value',
            axisTick: {show: false},
            axisLabel: {textStyle: {color: "white", fontSize: 8}},
            axisLine: {show: true, lineStyle: {color: '#0B3148', width: 1, type: 'solid'}},
            splitLine: {show: false}
        },
        series: [{
            name: '网络',
            type: 'line',
            itemStyle: {normal: {color: '#F3891B'}, lineStyle: {normal: {color: '#F3891B', opacity: 1}}},
            data: [120, 132, 101, 134, 90, 230, 210]
        }, {
            name: '内存',
            type: 'line',
            itemStyle: {normal: {color: '#006AD4'}, lineStyle: {normal: {color: '#F3891B', opacity: 1}}},
            data: [220, 182, 191, 234, 290, 330, 310]
        }, {
            name: 'CPU',
            type: 'line',
            itemStyle: {normal: {color: '#009895'}, lineStyle: {normal: {color: '#009895', opacity: 1}}},
            data: [150, 232, 201, 154, 190, 330, 410]
        }]
    });
}

function get10MinutesScale() {
    var currDate = new Date();
    var odd = currDate.getMinutes() % 10;
    var returnArr = new Array();
    currDate.setMinutes(currDate.getMinutes() - odd);
    for (var i = 0; i < 7; i++) {
        returnArr.push(currDate.getHours() + ":" + (currDate.getMinutes() < 10 ? ("0" + currDate.getMinutes()) : currDate.getMinutes()));
        currDate.setMinutes(currDate.getMinutes() - 10);
    }
    return returnArr;
}

function getLatestDays(num) {
    var currentDay = new Date();
    var returnDays = [];
    for (var i = 0; i < num; i++) {
        currentDay.setDate(currentDay.getDate() - 1);
        returnDays.push((currentDay.getMonth() + 1) + "/" + currentDay.getDate());
    }
    return returnDays;
}