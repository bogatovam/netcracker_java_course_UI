$(document).ready(function() {
	$("#q_equation").on("submit", calculate);
	$("tr").on('click', delRow);
	$("th").addClass("history_table_th");
	$("tr").addClass("history_table_tr");
});

function delRow(event) {
		if(!$("th").is($(event.target)))
    		$(event.currentTarget).remove();
}

function saveInTable(coeffs, result) {
	var newRow = document.createElement("tr");
	$("#tab1").append(newRow);

	for(var i = coeffs.length - 1; i > -1; --i){
		var newCell = newRow.insertCell();
		newCell.class = "history_table_td";
		newCell.value = coeffs[i];
		newCell.innerText = coeffs[i];
	}
	for(var i = 0; i < result.length; ++i){
		var newCell = newRow.insertCell();
		newCell.class = "history_table_td";
		newCell.value = result[i];
		newCell.innerText = result[i];
	}
	newRow.class = "history_table_tr";
	newRow.addEventListener('click', delRow, false);
}

function isValideNumber(num) {
	return num != "" && !isNaN(num);
}

function validationOfValues(coeffs) {
	var res = true;
	var errorMessage = "";
	for(var i = coeffs.length - 1; i >= 0; --i) {
		if(!isValideNumber(coeffs[i].val())) {
			coeffs[i].addClass("badValue");
			errorMessage+= "[" + (i + 1) +"] coefficient '";
			errorMessage+= coeffs[i].val() + "' is not a number<br>";
			res = false;
		} else coeffs[i].addClass("goodValue");
	}

	if(coeffs[2].val() == "0") {
		errorMessage+="The equation should be square, coefficient А is zero<br>";
		coeffs[2].addClass("badValue");
		res = false;;
	}
	console.log(errorMessage);
	errorMessage+= "Please enter valid numerical coefficient";
	$("#help_message").html(errorMessage);
	return res;
}

function calculate(event) {
	event.preventDefault();

	$("#x1").text("");
	$("#x2").text("");

	var coeffs = [];	
	coeffs[2] = $("#a_coeff");
	coeffs[1] = $("#b_coeff");
	coeffs[0] = $("#c_coeff");

	if(validationOfValues(coeffs)) {
		var result =[];
		coeffs[2] = $("#a_coeff").val();
		coeffs[1] = $("#b_coeff").val();
		coeffs[0] = $("#c_coeff").val();
		var d = Math.sqrt(coeffs[1]*coeffs[1] - 4*coeffs[2]*coeffs[0]);
		if(isNaN(d)){
			console.log("Quadratic equation has no real roots" + "sqrt(" + 
			coeffs[1]+"*"+coeffs[1] +"-"+ 4+"*"+coeffs[2]+"*"+coeffs[0]+") < 0")
			$("#help_message").text("Quadratic equation has no real roots");
			return;
		} else if(d == 0) {
			result[0] = (-coeffs[1] / (2*coeffs[2])).toFixed(5);
			result[1] = result[0];
		} else {
			result[0] = ((-coeffs[1] + d) / (2*coeffs[2])).toFixed(5);
			result[1] = ((-coeffs[1] - d) / (2*coeffs[2])).toFixed(5);
		}
		$("#help_message").text("The quadratic equation has the following roots: ");
		$("#x1").text("x1 = " + result[0]);
		$("#x2").text("x2 = " + result[1]);
		saveInTable(coeffs, result);
	}
}