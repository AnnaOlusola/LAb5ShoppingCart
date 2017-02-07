package controllers;

import controllers.security.CheckIfCustomer;
import controllers.security.Secured;
import models.products.Product;
import models.shopping.Basket;
import models.shopping.ShopOrder;
import models.users.Customer;
import models.users.User;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.*;

// Import models
// Import security controllers
//Authenticate user
@Security.Authenticated(Secured.class)
//Authorise user (check if user is a customer)
@With(CheckIfCustomer.class)

public class ShoppingCtrl extends Controller {
    
    // Get a user - if logged in email will be set in the session
	private Customer getCurrentUser() {
		return (Customer)User.getLoggedIn(session().get("email"));
	}

    @Transactional
    public Result addToBasket(Long id){

        //Find the product
        Product p = Product.find.byId;

        //Get basket for logged in customer
        Customer customer = (Customer)User.getLoggedIn(session().get("email"));

        //Check if item in the basket
        if (customer.getBasket() == null){
            //If no basket, create one
            customer.setBasket(new Basket());
            customer.getBasket().setCustomer(customer);
            customer.update();

        }
        // Add product to the basket and save
        customer.getBasket().addProduct(p);
        customer.update();

        // Show the basket contents
        return ok(basket.render(customer));

    }

    

    



    // Empty Basket
    @Transactional
    public Result emptyBasket() {
        
        Customer c = getCurrentUser();
        c.getBasket().removeAllItems();
        c.getBasket().update();
        
        return ok(basket.render(c));
    }


    
    // View an individual order
    @Transactional
    public Result viewOrder(long id) {
        ShopOrder order = ShopOrder.find.byId(id);
        return ok(orderConfirmed.render(getCurrentUser(), order));
    }

}