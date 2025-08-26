const stripe = Stripe('pk_test_51RxeYX2RBzJ8k86VpRve0JSqlZ2ILVlopjtKkO83OqUez6vIBxRczKHeCvAsgF9XJOzhmIpfpSfhwCgWRKEuzvGP006X3BG9jQ');
const paymentButton = document.querySelector('#paymentButton');
paymentButton.addEventListener('click', () => {
	stripe.redirectToCheckout({
		sessionId: sessionId
	})
});