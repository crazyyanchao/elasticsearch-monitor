// 从POST接口获取数据并以JSON的方式呈现到页面
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

function clickToBigdesk() {
    $('#layer03_right_label').click(function () {
        window.location.href = "bigdesk-index";
    });
}


