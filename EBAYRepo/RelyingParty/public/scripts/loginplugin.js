/**
 * http://usejsdoc.org/
 */

//counter to increment account id
var accntid = 10000;

//For Login
function login(phone, password) {
	alert("Username: " +phone);
	var obj = {};
	obj.phone = phone;
	obj.pwd = password;
	

	if (typeof(Storage) !== "undefined") {
		if(phone === localStorage.getItem("phone")){
			if(localStorage.getItem("isRegistered") === "true"){
				//Call Push notify Remote API
				var obj2 = {};
				obj2.phone = phone;
				obj2.vname = localStorage.getItem("vname");
					
				$.ajax({
			        cache : false,
			        type : "POST",
			        headers : {
						'content-type' : 'application/json',
						'accept' : 'application/json'
					},
					async : false,
					// setup the server address
			        url : 'https://localhost:3000/pushnotifytoauthenticate',
			        data : JSON.stringify(obj2),
			        success : function(result) {
			            alert("Push Notified Device To authenticate!");
			           },
			        error : function(result) {
			        	alert("Error!");
			        }
			    });
				
				//Wait for 10 secs and then keep checking for registration response.
				var delay=10000; //10 seconds
				
				//Listening for authentication response from JAVA FIDO Server
				setTimeout(function() {
				function callNode() {
				    $.ajax({
				        cache : false,
				        type : "POST",
				        headers : {
							'content-type' : 'application/json',
							'accept' : 'application/json'
						},
						async : false,
						// setup the server address
				        url : 'https://localhost:3000/listenForAuthResponse',
				        data : JSON.stringify(obj),
				        success : function(result) {
				            if(result){
				            	if(result == "SUCCESS"){
				            		alert("Authentication Response is Success");
				            		window.location.href = 'registered_account';
				            	}
				            	else {
				            		alert("No Response Yet");
				            	}	
				            }
				            // make new after every 10 secs
				            setTimeout(function() {
				            	callNode();
				            }, 10000);
				        },
				        error: function(result){
				        	alert("Failure");
				        }
				    });
				};
				callNode();	
			}, delay);
			}
			else {
				$.ajax({
					type : "POST",
					url : 'https://localhost:3000/login',
					data : JSON.stringify(obj),
					async : false,
					headers : {
						'content-type' : 'application/json',
						'accept' : 'application/json'
					},
					success : function(result) {
						if (result) {
							alert(result);
							if (typeof(Storage) !== "undefined") {
							    // Store
							    localStorage.setItem("uname", "Jasmira");
							    localStorage.setItem("vname", "HDFC");
							    localStorage.setItem("phone", phone);
							    localStorage.setItem("isRegistered", "false");
							    localStorage.setItem("accountId", accntid+1);
							}
							window.location.href = 'account';
						} else {
							alert("No Data");
						}
					},
					error : function(result) {
						alert("error");
					}
				});
			}
		}
		else {
		$.ajax({
			type : "POST",
			url : 'https://localhost:3000/login',
			data : JSON.stringify(obj),
			async : false,
			headers : {
				'content-type' : 'application/json',
				'accept' : 'application/json'
			},
			success : function(result) {
				if (result) {
					alert(result);
					if (typeof(Storage) !== "undefined") {
					    // Store
					    localStorage.setItem("uname", "Jasmira");
					    localStorage.setItem("vname", "HDFC");
					    localStorage.setItem("phone", phone);
					    localStorage.setItem("isRegistered", "false");
					    localStorage.setItem("accountId", accntid+1);
					}
					window.location.href = 'account';
				} else {
					alert("No Data");
				}
			},
			error : function(result) {
				alert("error");
			}
		});
		}
	}
}

// For Sign-up
/*function signup(username, password){
	alert("Username: " +username+ "   Password: " +password);
	var obj = {};
	obj.username = username;
	obj.password = password;
	
	$.ajax({
		type : "POST",
		url : 'http://localhost:3000/signup',
		data : JSON.stringify(obj),
		async : false,
		headers : {
			'content-type' : 'application/json',
			'accept' : 'application/json'
		},
		success : function(result) {
			alert("success");
			alert(result);
			if (result) {
				//alert(result);
				if(result === 'User Signed Up sucessfully.'){
					alert(result);
					window.location.href = 'account';
				}
				else {
					alert(result);
				}
				
			} else {
				alert("No Data");
			}
		},
		error : function(result) {
			alert("error");
		}
	});
	
}*/