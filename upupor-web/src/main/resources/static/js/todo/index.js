/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

$(window).on('load', function() {
    addTodo();
});


function markTodo(todoId){
    $.cvPostJson('/todo/markTodo', {todoId}, function (res) {
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

        $.cvPostJson('/todo/add', addTodo, function (res) {
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
            $.cvPostJson('/todo/delete', {todoId}, function (res) {
                if (respSuccess(res)) {
                    history.go();
                } else {
                    $.cvError("删除失败")
                }
            })
        }
    });


}
