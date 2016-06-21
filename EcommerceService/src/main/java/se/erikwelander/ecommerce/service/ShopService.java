/////////////////////////////////////////////////////////////////////////////
// Name:        CustomerRepository.java
// Encoding:	UTF-8
//
// Purpose:     Public APIs for the shop service.
//              This is the window to the outside world when you use the shop.
//              The service is applies logic to the interface APIs.
//              It is unaware of the underlying implementation,
//              so the backend logic can be replaced without ever touching the public APIs.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.service;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.exception.ShopServiceException;
import se.erikwelander.ecommerce.model.Customer;
import se.erikwelander.ecommerce.model.Order;
import se.erikwelander.ecommerce.model.Product;
import se.erikwelander.ecommerce.repository.CustomerRepository;
import se.erikwelander.ecommerce.repository.OrderRepository;
import se.erikwelander.ecommerce.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ShopService
{
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    private final AtomicInteger productIDGenerator;
    private final AtomicInteger orderIDGenerator;

    public ShopService (final CustomerRepository customerRepository,
                        final ProductRepository productRepository,
                        final OrderRepository orderRepository)
    {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;

        try
        {
            productIDGenerator = new AtomicInteger(productRepository.getHighestProductId());
            orderIDGenerator = new AtomicInteger(orderRepository.getHighestOrderId());

            System.out.println();
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not instantiate ShopService: " + exception.getMessage(), exception);
        }
    }

    public synchronized Product addProduct (final Product product)
    {
        try
        {
            if (productRepository.getProduct(product.id).equals(product))
            {
                throw new ShopServiceException("Could not add product: A product with id " + product.id + " already exists!");
            }
        } catch (final RepositoryException exception)
        {
        }

        final Product addProduct = new Product(getNextProductId(),
                product.getQuantity(),
                product.price,
                product.title,
                product.category,
                product.manufacturer,
                product.description,
                product.image);
        try
        {
            productRepository.addProduct(addProduct);
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not add product: " + exception.getMessage(), exception);
        }
        return addProduct;
    }

    public synchronized Product getProduct (final int productId)
    {
        try
        {
            return productRepository.getProduct(productId);
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not getProduct: " + exception.getMessage(), exception);
        }
    }

    public synchronized List<Product> getAllProducts ()
    {
        try
        {
            return productRepository.getAllproducts();
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not get products.: " + exception.getMessage(), exception);
        }
    }

    public synchronized void updateProduct (final Product product)
    {
        try
        {
            productRepository.updateProduct(product);
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not updateProduct: " + exception.getMessage(), exception);
        }
    }

    public synchronized void removeProduct (final int productId)
    {
        try
        {
            for (Customer customer : customerRepository.getAllCustomers())
            {
                customer.removeFromShoppingCart(productId);
                updateCustomer(customer);
            }
            productRepository.removeProduct(productId);
        } catch (final Exception exception)
        {
            throw new ShopServiceException("Could not remove product: " + exception.getMessage(), exception);
        }
    }


    public synchronized void addCustomer (final Customer customer)
    {
        try
        {
            customerRepository.addCustomer(customer);
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not add customer: " + exception.getMessage(), exception);
        }
    }

    public synchronized Customer getCustomer (final String customerUsername)
    {
        try
        {
            return customerRepository.getCustomer(customerUsername);
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not get customer: " + exception.getMessage(), exception);
        }
    }

    public synchronized void updateCustomer (final Customer customer)
    {
        try
        {
            customerRepository.updateCustomer(customer);
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not update customer: " + exception.getMessage(), exception);
        }
    }

    public synchronized void addProductToCustomer (final int productId, final String customerUsername, final int amount)
    {
        try
        {
            if (productRepository.getProduct(productId).getQuantity() >= amount)
            {
                final Customer customer = customerRepository.getCustomer(customerUsername);
                for (int i = 0; i < amount; i++)
                {
                    customer.addToShoppingCart(productId);
                }
                // Make the repository record the changes to customer
                customerRepository.updateCustomer(customer);
            }
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not add product to customer: " + exception.getMessage(), exception);
        }
    }

    public synchronized void removeCustomer (final String customerUsername)
    {
        try
        {
            customerRepository.removeCustomer(customerUsername);
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not remove customer: " + exception.getMessage(), exception);
        }
    }


    public synchronized Order createOrder (final String customerUsername)
    {
        final Order newOrder;
        try
        {
            final Customer customer = customerRepository.getCustomer(customerUsername);
            ArrayList<Integer> orderedProductIds = customer.getAllShoppingCartItems();
            if (orderedProductIds.isEmpty())
            {
                throw new ShopServiceException("This user has no items in their cart");
            }
            productRepository.productsUpdateQuantity(orderedProductIds, -1);
            try
            {
                newOrder = new Order(getNextOrderId(), customerUsername, orderedProductIds);
                orderRepository.addOrder(newOrder);
                try
                {
                    customer.emptyShoppingCart();
                    customerRepository.updateCustomer(customer);
                } catch (final RepositoryException exception)
                {
                    //orderRepository.removeOrder(orderId);
                    throw new ShopServiceException("Could not update customer with new order: " + exception.getMessage(), exception);
                }
            } catch (final RepositoryException exception)
            {
                productRepository.productsUpdateQuantity(orderedProductIds, 1);
                throw new ShopServiceException("Could not add new order: " + exception.getMessage(), exception);
            }
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not create order: " + exception.getMessage(), exception);
        }
        return newOrder;
    }

    public synchronized Order getOrder (final int orderId)
    {
        try
        {
            return orderRepository.getOrder(orderId);
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not get order: " + exception.getMessage(), exception);
        }
    }

    public synchronized List<Order> getOrdersFromUser (final String customerUsername)
    {
        try
        {
            customerRepository.getCustomer(customerUsername);
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not get orders since user does not exist: " + exception.getMessage(), exception);
        }

        try
        {
            return orderRepository.getAllOrders(customerUsername);
        } catch (final RepositoryException exception)
        {
            List<Order> emptyOrdersList = new ArrayList<>();
            return emptyOrdersList;
        }
    }

    public synchronized void updateOrder (final Order order)
    {
        try
        {
            orderRepository.updateOrder(order);
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not update order: " + exception.getMessage(), exception);
        }
    }

    public synchronized void removeOrder (final int orderId)
    {
        try
        {
            orderRepository.removeOrder(orderId);
        } catch (final RepositoryException exception)
        {
            throw new ShopServiceException("Could not remove order: " + exception.getMessage(), exception);
        }
    }

    private int getNextProductId ()
    {
        return productIDGenerator.incrementAndGet();
    }

    private int getNextOrderId ()
    {
        return orderIDGenerator.incrementAndGet();
    }
}