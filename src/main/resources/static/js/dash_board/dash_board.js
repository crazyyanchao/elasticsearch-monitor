// 从POST接口获取数据并以JSON的方式呈现到页面
// 刷新HEAD概览-统计量请求
function clusterStats() {
    var address = sessionStorage.getItem("esClusterAddress");
    var obj = {};
    obj.address = address;
    var json = JSON.stringify(obj);
    $("#loading").show();
    $.ajax({
        type: 'POST',
        async: true,
        url: 'cluster-basis-info',
        data: json,
        contentType: "application/json",
        success: function (data) {
            var result = JSON.parse(data.result);
            // 刷新集群概览数据
            $("#layer02_01 .clusterStats").html(result.cluster_name_status);
            $("#layer02_02 .clusterDocs").html(result.docs);
            $("#layer02_03 .clusterStore").html(result.store);
            $("#layer02_04 .clusterShards").html(result.shards);
            $("#layer02_05 .clusterNodes").html(result.nodes);
            $("#layer02_06 .clusterIndices").html(result.indices);

        },
        dataType: 'json'
    });
}

// 刷新表格数据
function clusterIndicesTimeLine() {
    var address = sessionStorage.getItem("esClusterAddress");
    var obj = {};
    obj.address = address;
    var json = JSON.stringify(obj);
    $.ajax({
        type: 'POST',
        async: true,
        url: 'indices-time-info',
        data: json,
        contentType: "application/json",
        success: function (data) {
            var result = JSON.parse(JSON.stringify(data.result));
            console.log(data.result);

            // 刷新表格数据
            console.log(result.monitor_caiji_all.earliest);
            $("#layer04_left .table-cell-earliest-monitor_caiji_all").html(result.monitor_caiji_all.earliest);
            $("#layer04_left .table-cell-latest-monitor_caiji_all").html(result.monitor_caiji_all.latest);

            $("#layer04_left .table-cell-earliest-monitor_caiji_small").html(result.monitor_caiji_small.earliest);
            $("#layer04_left .table-cell-latest-monitor_caiji_small").html(result.monitor_caiji_small.latest);

            $("#layer04_left .table-cell-earliest-monitor_caiji_preprocess").html(result.monitor_caiji_preprocess.earliest);
            $("#layer04_left .table-cell-latest-monitor_caiji_preprocess").html(result.monitor_caiji_preprocess.latest);

            $("#layer04_left .table-cell-earliest-zdr_caiji").html(result.zdr_caiji.earliest);
            $("#layer04_left .table-cell-latest-zdr_caiji").html(result.zdr_caiji.latest);

            $("#layer04_left .table-cell-earliest-zdr_data").html(result.zdr_data.earliest);
            $("#layer04_left .table-cell-latest-zdr_data").html(result.zdr_data.latest);

            $("#layer04_left .table-cell-earliest-common_caiji").html(result.common_caiji.earliest);
            $("#layer04_left .table-cell-latest-common_caiji").html(result.common_caiji.latest);

            $("#layer04_left .table-cell-earliest-event_data").html(result.event_data.earliest);
            $("#layer04_left .table-cell-latest-event_data").html(result.event_data.latest);

            $("#layer04_left .table-cell-earliest-monitor_data").html(result.monitor_data.earliest);
            $("#layer04_left .table-cell-latest-monitor_data").html(result.monitor_data.latest);

            $("#layer04_left .table-cell-earliest-other").html("-");
            $("#layer04_left .table-cell-latest-other").html("-");
        },
        dataType: 'json'
    });
}

function tableMouseover() {
    // $("#layer04_left .table-cell-earliest-monitor_caiji_all").on("mouseover", function () {
    //     //addToolTip($("#layer04_left .table-cell-earliest-monitor_caiji_all"));
    // }).click(function () {
    //     // 点击之后查看采集异常-最早数据和最近数据
    //     sessionStorage.setItem("temp-json-data", null);
    //     // 使用JSON查看页面查看数据
    //     window.location.href = "json";
    // });

    $(".table-row").on("mouseover", function () {
        // openDialog();
        //setTimeout(openDialog(),2000);
    }).on("mouseout", function () {
        //setTimeout(closeDialog(),2000);
    }).click(function () {

        var indexType = $(this).find(".table-cell").eq(1).html();
        openDialog(indexType);

        // // 点击之后查看采集异常-最早数据和最近数据
        // sessionStorage.setItem("temp-json-data", null);
        // // 使用JSON查看页面查看数据
        // window.location.href = "json";
    });
}

// 根据索引类型查看索引中异常的数据
function setAbnormalDataDescription(indexType) {
    var address = sessionStorage.getItem("esClusterAddress");
    var obj = {};
    obj.address = address;
    obj.indexType = indexType;
    var json = JSON.stringify(obj);
    $("#loading").show();
    $.ajax({
        type: 'POST',
        async: false,
        url: 'abnormal-data-description',
        data: json,
        contentType: "application/json",
        success: function (data) {
            sessionStorage.setItem("temp-json-data", JSON.stringify(data));
            $("#light-json-renderer").load("json", function () {
                $("#light-json-renderer #loading").hide();
                checkJson();
            });
        },
        dataType: 'json'
    });
}

function openDialog(indexType) {
    document.getElementById('light').style.display = 'block';
    document.getElementById('fade').style.display = 'block';
    // 缓存异常数据供给JSON查看
    setAbnormalDataDescription(indexType);
}

function closeDialog() {
    document.getElementById('light').style.display = 'none';
    document.getElementById('fade').style.display = 'none';
}

function checkJsonInner() {
    // // 点击之后查看采集异常-最早数据和最近数据
    // // 使用JSON查看页面查看数据
    window.location.href = "json";
}

function clickToBigdesk() {
    $('#layer03_left_label01').click(function () {
        window.location.href = "bigdesk-index";
    });
}

