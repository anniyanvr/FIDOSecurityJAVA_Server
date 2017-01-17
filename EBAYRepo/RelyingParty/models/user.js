/**
 * http://usejsdoc.org/
 */
/** MONGOOSE DB ***********************/
var mongoose = require('mongoose');
var bcrypt = require('bcryptjs');

mongoose.connect('mongodb://localhost/rpdatabase');

var Schema = mongoose.Schema;
//create a schema
var userSchema = new Schema({
username: { type: String, required: true },
password: { type: String, required: true },
vendorname: { type: String },
phonenumber: { type: String, required: true, unique: true },
email: { type: String, required: true },
issignedup: Boolean,
isfidoregistered: Boolean
//accountid: { type: String }
});

//Customer methods
userSchema.methods.dudify = function() {
	  // add some stuff to the users name
	  this.name = this.name + '-dude'; 

	  return this.name;
};

//Custom method to add new user during signup
userSchema.methods.create = function(uname, pwd, vname, phone, email) {
	  // add some stuff to the users name
	User.find({ username: uname }, function(err, user) {
		  if (err) {
			  throw err;
		  }

		 /* if (user) {
			  console.log("Username already exist.");
			  window.location.href = 'register';
		  }*/
		  else {
			  var user = new User({
				  username: uname,
				  password: bcrypt.hashSync(pwd, 10),
				  vendorname: vname,
				  phonenumber: phone,
				  email: email,
				  issignedup: true,
				  isfidoregistered: false
			  	});
	
			  user.save(function(err) {
				  if (err) {
					  throw err;
				  }
				  else{
					  console.log('\nUser signed up successfully!');
					  return true;
				  }
				  
			  });
			  return true;
		  	}
	});
};

//Custom method to authenticate user during login
userSchema.methods.authenticate = function(uname, pwd) {
	User.find({ username: uname }, function(err, user) {
		  if (err) {
			  throw err;
		  }

		  else {
			// object of the user
			  console.log(user);
			  if (bcrypt.compareSync(pwd, user.pwd)){
				  console.log("Authorized User.");
				  windows.location.href = 'account';
			  }
			  else{
				  console.log("Invalid Username or Password.");
				  window.location.href = 'index';
			  }  
		  }
		});
};
	
//the schema is useless so far, we need to create a model using it
var User = mongoose.model('User', userSchema);

//make this available to our users in our Node applications
module.exports = User;


/** MONGOOSE DB ***********************/