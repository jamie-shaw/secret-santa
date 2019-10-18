<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="images/SantaPointing2.gif" alt="Santa Pointing" />
</aside>

<section id="form-container">

    <form action="/changePassword" method="post">

        <label for="NewPassword">Enter your new password:</label>
        <input type="password" name="password" size="15" />
        
        <label for="Password">Enter new password again:</label>
        <input type="password" name="confirmPassword" size="15" />
        
        <button type="submit">Save My New Password</button>
       
    </form>

</section>
