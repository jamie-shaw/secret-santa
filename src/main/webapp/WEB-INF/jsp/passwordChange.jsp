<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>


<aside id="image-container">
    <img border="0" src="images/SantaPointing2.gif" alt="Santa Pointing" />
</aside>

<section id="form-container">

    <form action="reset-password" method="post">
        <input type="hidden" 
        <label for="NewPassword">Enter your new password:</label>
        <input type="password" name="NewPassword" size="15" id="NewPassword" />
        
        <label for="Password">Enter new password again:</label>
        <input type="password" name="NewPassword2" size="15"  id="NewPassword2" />
        
        <button type="submit">Save My New Password</button>
       
    </form>

</section>


            