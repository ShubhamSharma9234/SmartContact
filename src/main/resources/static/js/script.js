/**
 * 
 */
const toggleSidebar = () => {
	if ($(".sidebar").is(":visible")) {
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	} else {


		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
};

const search = () => {
	console.log("searching...");
	let query = $("#search-input").val();

	if (query == "") {
		$(".search-result").hide();
	}
	else {
		console.log(query);

		//semding request to server

		let url = `http://localhost:8080/search/${query}`;

		fetch(url)
			.then((response) => {
				return response.json();
			}).then((data) => {

				console.log(data);

				let text = `<div class='list-group'>`;

				data.forEach((contact) => {
					text += `<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'>${contact.firstName}</a>`
				});

				text += `</div>`;

				$(".search-result").html(text);
				$(".search-result").show();

			});
	}

};

// first request to server to create order

const paymentStart = () => {

	console.log('payment Started');

	let amount = $('#payment_field').val();

	console.log(amount);

	if (amount == null || amount == '') {
		
		swal('amount is required','error');

		return;
	}
	// we will use ajax to send request to server to create orders

	$.ajax({
		url: "/user/create_order",
		data: JSON.stringify({ amount: amount, info: "order_request" }),
		contentType: "application/json",
		type: "POST",
		dataType: "json",
		success: function(response) {
			//invoke when success
			console.log(response)
			if (response.status == "created") {
				// open payement form
				
				var options = {

					"key": "rzp_test_JveLFJpZmG0SEz", // Enter the Key ID generated from the Dashboard
					"amount": response.amount, // Amount is in currency subunits. Default currency is INR. Hence, 50000 refers to 50000 paise
					"currency": "INR",
					"name": "Smart Contact Manager",
					"description": "Transction Amount",
					"image": "http://icongal.com/gallery/image/11891/money_cash_payment.png",
					"order_id": response.id, //This is a sample Order ID. Pass the `id` obtained in the response of Step 1
					"handler": function(response) {
						alert(response.razorpay_payment_id);
						alert(response.razorpay_order_id);
						alert(response.razorpay_signature)
						console.log("payment successfull !!! ");
						swal("congrats !!! payment successfull !!! ","success");
						updatePaymentServer(
							response.razorpay_payment_id,
							response.razorpay_order_id,
							"paid"
						);
						
					},
					"prefill": {
						"name": "",
						"email": "",
						"contact": ""
					},
					"notes": {
						"address": "Learn code with Durgesh "
					},
					"theme": {
						"color": "#3399cc"
					}
				};

				var rzp = new Razorpay(options); // payment intiate

				rzp.on('payment.failed', function(response) {
					alert(response.error.code);
					alert(response.error.description);
					alert(response.error.source);
					alert(response.error.step);
					alert(response.error.reason);
					alert(response.error.metadata.order_id);
					alert(response.error.metadata.payment_id);
					swal("Oops payment failed !!! ","error");
					
				});

				rzp.open(); // it will open the razor payment page 

			}
		},
		error: function(error) {
			//invoke when error
			console.log(error)
			alert('something went wrong !!! ')
		}
	})
};


function updatePaymentServer(payment_id,order_id,status){
	
	$.ajax({
		url: "/user/update_order",
		data: JSON.stringify({ payment_id: payment_id, order_id: "order_id",status : status }),
		contentType: "application/json",
		type: "POST",
		dataType: "json",
		success : function(response){
			swal("Congrates!!! Your Payment successfull","success");
		},
		error : function(error){
			swal("Your payment is successfull ,but we did not get on server, we will contact you as soon as possible","error");
		},
			
		
	});
};