(function($) {
	$.extend({
		// 提取中文英文数字
		strip : function(s) {
			return s.match(/([\u4e00-\u9fa5\w]*)/ig).join("");
		},
		// 获取/设置HttpSession
		session : function(name, val) {
			// 获取
			if (arguments.length == 1) {
				var res = "";
				$.ajax({
					type : "POST",
					async : false,
					url : "demo/sessionget",
					data : {
						name : name
					},
					success : function(data) {
						res = data;
					}
				});
				return res;
			} // 设置
			else if (arguments.length == 2) {
				$.ajax({
					type : "POST",
					async : false,
					url : "demo/sessionset",
					data : {
						name : name,
						val : val
					},
					success : function(data) {
					}
				});

			}
		},
		// 获取指定表单所有项值
		getFormVals : function(fm) {
			var data = {};
			$(fm + " [name]").each(function(i, n) {
				var name = $(n).attr("name");
				data[name] = $.trim($(n).val());
			});
			return data;
		},
		// 根据json数据设置表单所有项值
		setFormVals : function(fm, data) {
			$(fm + " [name]").each(function(i, n) {
				var name = $(n).attr("name");
				$(n).val($.trim(data[name]));
			});
		},
		// 清空指定表单所有项值
		clearFormVals : function(fm) {
			$(fm + " [name]").each(function(i, n) {
				$(n).val("");
			});
		},
		// 根据data填充table-row 返回html
		getTableRow : function(rowdata, colnames) {
			var html = "";
			$.each(colnames, function(i, n) {
				var name = n;
				var anno = "";
				if (n.indexOf("@") >= 0) {
					name = n.split("@")[0];
					anno = n.split("@")[1];
				}
				// 根据anno处理该列的值
				if (anno == "date") {
					html += "<td>" + $.msfmt.todate(rowdata[name], false, 8)
							+ "</td>";
				} else if (anno == "fulltime") {
					html += "<td>" + $.msfmt.todate(rowdata[name], true, 8)
							+ "</td>";
				} else if (anno == "") {
					html += "<td>" + rowdata[name] + "</td>";
				}
			});
			return html;
		},
		// 根据row-data填充模板行
		getTmplRow : function(tmplrow, rowdata) {
			$.each(rowdata, function(k, v) {
				tmplrow = tmplrow.replace("@" + k, v);
			});
			return tmplrow;
		},
		// 获取列表选中项value[单选]
		getSelectVal : function(listid, chkname) {
			var val = "";
			$(listid + " [name='" + chkname + "']").each(function(i, n) {
				if (n.checked)
					val = n.value;
			});
			return val;
		}
	});
}(jQuery));