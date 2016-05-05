/*********************************************************************************************/
/* FormValidator                                                                             */
/* by Henry Yu  (yuhaodong@gmail.com)                                                        */
/* http://www.beetlesoft.net                                                                 */
/* regex type:[EMAIL||NUMBER||PHONE||POSTCODE||CURRENCY||DATE||TIME||DATETIME]               */
/* eg:<input type="text" name="mail" required="1" regex="EMAIL" message="Please enter a mail !">*/
/********************************************************************************************/
function formValidate(targetForm) {
    var EMAIL = "^[a-zA-Z0-9_-]+(\.([a-zA-Z0-9_-])+)*@[a-zA-Z0-9_-]+[.][a-zA-Z0-9_-]+([.][a-zA-Z0-9_-]+)*$"
    var NUMBER = "^[0-9]*$"
	var PHONE =/^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/
	var POSTCODE=/^[a-zA-Z0-9 ]{3,12}$/
    for (var i = 0; i < targetForm.elements.length; i++) {
	if(targetForm.elements[i].getAttribute("strValue") != null) {
	    var message = targetForm.elements[i].getAttribute("message");
	    var strBound = targetForm.elements[i].getAttribute("strBound");	
	    var strVal = targetForm.elements[i].getAttribute("strValue");
	    if(eval('document.' + strBound + '.type') == 'select-one') {
		var sIndex = eval('document.' + strBound + '.selectedIndex');
		var strBoundVal = eval('document.' + strBound + '[' + sIndex + '].value'); 
	    }
	    if(strVal == strBoundVal){
		if(targetForm.elements[i].value == '') {
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;
		}						
	    }
	}
	if(targetForm.elements[i].getAttribute("required")) {
	    var message = targetForm.elements[i].getAttribute("message");
		if(message==null){
			message="This field can't be null!";
		}
	    if(targetForm.elements[i].type == 'checkbox') {
		if(!targetForm.elements[i].checked) {
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;
		}
	    }
	    else if(targetForm.elements[i].type == 'text' || targetForm.elements[i].type == 'password' || targetForm.elements[i].type == 'file') {
		if(targetForm.elements[i].value == '') {
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;								
		}
		}
	    else if(targetForm.elements[i].type == 'select-one') {
		if(targetForm.elements[i].value == '') {
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;								
		}
	    }
	    else if(targetForm.elements[i].type == 'textarea') {
		if(targetForm.elements[i].value == '') {
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;								
		}
	    }
	    else if(targetForm.elements[i].type == 'radio') {
		var isSelected = false;
		var j = 0;
		while(targetForm.elements[i+j].type == 'radio' && 
		      targetForm.elements[i].name == targetForm.elements[i+j].name) {
		    if(targetForm.elements[i+j].checked) {
			isSelected = true;
		    }
		    j++;
					
		}
		j = 0;
		while(targetForm.elements[i-j].type == 'radio' && 
		      targetForm.elements[i].name == targetForm.elements[i-j].name) {
		    if(targetForm.elements[i-j].checked) {
			isSelected = true;
		    }
		
		    if(i-j <= 0) {
			break;
		    }
		
		    j++;					
		}				
		if(!isSelected) {
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;				
		}
	    }
	    else {
		return true;
	    }
	}
	if (targetForm.elements[i].getAttribute("depRequired") != null) {
	    var o = document.getElementById(targetForm.elements[i].getAttribute('depRequired'));

	    if (o.value != null) {
		if (o.value.length > 0 && targetForm.elements[i].value.length <= 0 ) {
		    alert(targetForm.elements[i].getAttribute("depMessage"));
		    targetForm.elements[i].focus();	
		    return false;
		}
	    }
	}
	if (targetForm.elements[i].getAttribute("regex") != null) {
	    var message = targetForm.elements[i].getAttribute("message");
	    var UserRegEx = targetForm.elements[i].getAttribute("regex");
	    var InputValue = targetForm.elements[i].value;
	    if(UserRegEx == 'EMAIL') {
		var re = new RegExp(EMAIL);
		if(!InputValue.match(re)) {
			if(message==null){
					message="This mail address is invalid!";
				}
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;	
		}
	    }
	    else if(UserRegEx == 'NUMBER') {
		var re = new RegExp(NUMBER);
		if(!InputValue.match(re)) {
			 if(message==null){
					message="Must be a number!";
				}
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;	
		}
	    }
		else if(UserRegEx == 'PHONE') {
		var re = new RegExp(PHONE);
		if(!InputValue.match(re)) {
			if(message==null){
					message="This phone number format is invalid!";
			}
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;	
		}
	    }
		else if(UserRegEx == 'POSTCODE') {
		var re = new RegExp(POSTCODE);
		if(!InputValue.match(re)) {
			if(message==null){
					message="This postcode is invalid!";
			}
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;	
		}
	    }
		else if(UserRegEx == 'CURRENCY') {
		if(!ex_form_currency(InputValue)) {
			if(message==null){
					message="This currecy format is invalid!";
			}
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;	
		}
	    }
		else if(UserRegEx == 'TIME') {
		if(!ex_form_validateAdvancedTime(InputValue)) {
			if(message==null){
					message="Time [HH:MM:SS] format is invalid!";
			}
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;	
		}
	    }
		else if(UserRegEx == 'DATE') {
		if(!ex_form_validateDate(InputValue)) {
			if(message==null){
					message="Date [YYYY-MM-DD] format is invalid!";
			}
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;	
		}
	    }
		else if(UserRegEx == 'DATETIME') {
		if(!ex_form_validateDateTime(InputValue)) {
			if(message==null){
					message="Datetime [YYYY-MM-DD HH:MM:SS] format is invalid!";
			}
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;	
		}
	    }
	    else { //self define regexp
		var re = new RegExp(UserRegEx);
		if(!InputValue.match(re)) {
		    alert(message);
		    targetForm.elements[i].focus();	
		    return false;	
		}
	    }
	}
	}
    return true;		
}
//expand function
function ex_form_currency(fieldValue)
{
	if(fieldValue!=""){
   regex1 = /^[\d]+[\d\.]*[\d]+$/;
   regex2 = /^[\d]+$/;
    if(!regex1.test(fieldValue) && !regex2.test(fieldValue)) {
		return false;
    }
	}
	return true;
}
function ex_form_validateAdvancedTime(time){//HH:MM:SS
	if(time==""){
		return true;
	}
	if(time.indexOf(":")<0) return false;
	var status = ex_form_validateTime(time);
	if(status == false) 	return false;
	var seconds = time.substring(time.indexOf(":") + 4, time.length);
	if(ex_form_digits(seconds)) {			                              
		if((seconds <= 59) && (seconds >= 0)) 
			return true;
	}
	return false;	
}
function ex_form_validateTime(time) { //HH:MM
	if(time==""){
		return true;
	}
	var segments;			
	var hour;					
	var minute;				
	segments = time.split(":");
	if (segments.length >= 2) {
			hour = segments[0];
			if(!ex_form_digits(hour)) return false;
			if ((hour > 23) || (hour <= -1)) 
				return false;
			minute = segments[1];
			if(!ex_form_digits(minute)) return false;
			if (( minute <= 59) && (minute >= 0)) 
				return true;
	}
	return false;
}	
function ex_form_digits(fieldValue)
{
	var regex = /^[\d]+$/;
	if(!regex.test(fieldValue))
	{
		return false;
	}
	return true;
}
function ex_form_validateDate(time){//YYYY-MM-DD
	if(time==""){
		return true;
	}
	if(time.indexOf("-")<0) return false;
	var year;					
	var month;				
	var day;
	var segments = time.split("-");
	if (segments.length =3) {
			year = segments[0];
			if(!ex_form_digits(year)) return false;
			if ((year > 9999) || (year <= 0)) 
				return false;
			month = segments[1];
			if(!ex_form_digits(month)) return false;
			if (( month > 12) || (month < 0)) 
				return false;
			day = segments[2];
			if(!ex_form_digits(day)) return false;
			if (( day > 31) || (day <0)) 
				return false;
	}else{
		return false;
	}
	return true;
}
function ex_form_validateDateTime(datetime){//YYYY-MM-DD HH:MM:SS
	if(datetime==""){
		return true;
	}
	if(datetime.indexOf("-")<0) return false;
	if(datetime.indexOf(":")<0) return false;
	var segments = datetime.split(" ");
	if (segments.length =2) {
		 var date=	segments[0];
		 var time= segments[1];
		 if(!ex_form_validateDate(date)) return false;
         if(!ex_form_validateAdvancedTime(time)) return false;
	}else{
		return false;
	}
	return true;
}