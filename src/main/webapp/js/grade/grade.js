//消除iOS点击延迟
$(function () {
    FastClick.attach(document.body);
});

//进行首次默认查询
$(document).ready(function () {
    postQueryForm();
});

//清除原有的成绩信息
function clearGradeInfo() {
    //清空原学分绩点
    $(".page_desc:eq(0)").text("").hide();
    $(".page_desc:eq(1)").text("").hide();
    //清空原表格的内容
    $("tbody:eq(0)").children("tr").remove();
    $("tbody:eq(1)").children("tr").remove();
}

//处理成绩信息
function handleGradeInfo(gradeQueryResult) {
    //显示查询成功提示
    $("#toast").show().delay(1000).hide(0);
    //平均学分绩点
    $(".page_desc:eq(0)").text("平均学分绩点：" + gradeQueryResult.firstTermGPA).show();
    $(".page_desc:eq(1)").text("平均学分绩点：" + gradeQueryResult.secondTermGPA).show();
    //第一学期成绩列表
    var firstTermGradeList = gradeQueryResult.firstTermGradeList;
    for (var i = 0; i < firstTermGradeList.length; i++) {
        var gradeName = firstTermGradeList[i].gradeName;
        var gradeCredit = firstTermGradeList[i].gradeCredit;
        var gradeScore = firstTermGradeList[i].gradeScore;
        $("tbody:eq(0)").append("<tr><td style='width='65%;'>"
            + gradeName + "</td><td>"
            + gradeCredit + "</td><td>" + gradeScore + "</td></tr>");
    }
    //第二学期成绩列表
    var secondTermGradeList = gradeQueryResult.secondTermGradeList;
    for (var j = 0; j < secondTermGradeList.length; j++) {
        var gradeName = secondTermGradeList[j].gradeName;
        var gradeCredit = secondTermGradeList[j].gradeCredit;
        var gradeScore = secondTermGradeList[j].gradeScore;
        $("tbody:eq(1)").append("<tr><td style='width='65%;'>"
            + gradeName + "</td><td>"
            + gradeCredit + "</td><td>" + gradeScore + "</td></tr>");
    }
}

//提交异步查询请求
function postQueryForm(year) {
    //显示进度条
    changeYearSelectedClass(year);
    $("#loadingToast, .weui_mask").show();
    if (year == undefined) {
        $.ajax({
            url: "/api/gradequery",
            type: 'post',
            success: function (result) {
                //隐藏进度条
                $("#loadingToast, .weui_mask").hide();
                clearGradeInfo();
                if (result.success === true) {
                    changeYearSelectedClass(result.data.year);
                    handleGradeInfo(result.data);
                } else {
                    showCustomErrorTip(result.message);
                }
            },
            error: function (result) {
                //隐藏进度条
                $("#loadingToast, .weui_mask").hide();
                if (result.status) {
                    //网络连接超时
                    showCustomErrorTip(result.responseJSON.message);
                } else {
                    showNetworkErrorTip();
                }
            }
        });
    } else {
        $.ajax({
            url: "/api/gradequery",
            data: {year: year},
            type: 'post',
            success: function (result) {
                //隐藏进度条
                $("#loadingToast, .weui_mask").hide();
                clearGradeInfo();
                if (result.success === true) {
                    changeYearSelectedClass(result.data.year);
                    handleGradeInfo(result.data);
                } else {
                    showCustomErrorTip(result.message);
                }
            },
            error: function (result) {
                //隐藏进度条
                $("#loadingToast, .weui_mask").hide();
                if (result.status) {
                    //网络连接超时
                    showCustomErrorTip(result.responseJSON.message);
                } else {
                    showNetworkErrorTip();
                }
            }
        });
    }
}

//显示网络错误提示
function showNetworkErrorTip() {
    weui.alert('请求成绩信息失败，请检查网络连接', {
        title: '错误提示',
        buttons: [{
            label: '确定',
            type: 'primary'
        }]
    });
}

//显示自定义错误提示
function showCustomErrorTip(message) {
    weui.alert(message, {
        title: '错误提示',
        buttons: [{
            label: '确定',
            type: 'primary'
        }]
    });
}

//更改学年选中属性
function changeYearSelectedClass(region) {
    $(".selected").removeClass("selected");
    switch (region) {
        case 0:
            $("#one").addClass("selected");
            break;

        case 1:
            $("#two").addClass("selected");
            break;

        case 2:
            $("#three").addClass("selected");
            break;

        case 3:
            $("#four").addClass("selected");
            break;
    }
}

//更新实时成绩数据
function refreshGradeData() {
    $("#loadingToast, .weui_mask").show();
    $.ajax({
        url: '/api/refreshgrade',
        method: 'POST',
        success: function (result) {
            //隐藏进度条
            $("#loadingToast, .weui_mask").hide();
            clearGradeInfo();
            if (result.success === true) {
                changeYearSelectedClass(result.data.year);
                handleGradeInfo(result.data);
            } else {
                showCustomErrorTip(result.message);
            }
        },
        error: function (result) {
            //隐藏进度条
            $("#loadingToast, .weui_mask").hide();
            if (result.status) {
                //网络连接超时
                showCustomErrorTip(result.responseJSON.message);
            } else {
                showNetworkErrorTip();
            }
        }
    })
}

//显示更多设置
function showOptionMenu() {
    weui.actionSheet([
        {
            label: '管理缓存配置',
            onClick: function () {
                window.location.href = '/privacy';
            }
        }, {
            label: '更新实时数据',
            onClick: function () {
                refreshGradeData();
            }
        }
    ], [
        {
            label: '取消',
            onClick: function () {

            }
        }
    ]);
}