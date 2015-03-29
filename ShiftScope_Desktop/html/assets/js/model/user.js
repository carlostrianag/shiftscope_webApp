function User(data_object) {
	var object = new Object();
	
	object.facebookId = (data_object.facebookId)?data_object.facebookId:"";
	object.name = data_object.name;
	object.lastName = data_object.lastName;
	object.password = data_object.password;
	object.email = data_object.email;

	return object;
}