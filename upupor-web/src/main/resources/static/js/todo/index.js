$(window).on('load', function() {
    addTodo();
});


function markTodo(todoId){
    $.cvPost('/todo/markTodo', {todoId}, function (res) {
        if (respSuccess(res)) {
            if($("#"+todoId)[0].className.indexOf('text-decoration-line-through') > 0){
                $("#"+todoId).removeClass('text-decoration-line-through text-black-50');
                $("#"+todoId+'delete_forever').removeClass('visible').addClass('invisible');
            }else{
                $("#"+todoId).addClass('list-group-item text-decoration-line-through text-black-50');
                $("#"+todoId+'delete_forever').removeClass('invisible').addClass('visible');
            }
        } else {
            $.cvError("标记失败")
        }
    })
}

function addTodo(){
    $('form').submit(function (event) {
        let todo = $("#todo").val();
        let todoDetail = $("#todoDetail").val();

        if(cvIsNull(todo)){
            $.cvWarn("请输入待办");
            return false;
        }

        let addTodo = {
            todo,
            todoDetail
        };

        $.cvPost('/todo/add', addTodo, function (res) {
            if (respSuccess(res)) {
                $.cvSuccess("添加待办成功");
                setTimeout(function () {
                    history.go();
                }, 1600)
            } else {
                $.cvError("添加待办失败")
            }
        });

        // 不刷新
        return false;
    });
}


function deleteTodo(todoId) {
    swal({
        title: '确定永久删除吗',
        text: "提示",
        icon: "warning",
        buttons: [{
            text: "确认",
            value: true,
            visible: true,
        }, {
            text: "取消",
            value: false,
            visible: true,
        }],
        closeOnClickOutside: false,
        closeOnEsc: false,
    }).then((willDelete) => {
        if (willDelete) {
            $.cvPost('/todo/delete', {todoId}, function (res) {
                if (respSuccess(res)) {
                    history.go();
                } else {
                    $.cvError("删除失败")
                }
            })
        }
    });


}
