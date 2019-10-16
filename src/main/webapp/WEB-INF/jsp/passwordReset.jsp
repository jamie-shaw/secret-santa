<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="images/SantaPointing2.gif" alt="Santa Pointing" />
</aside>

<section id="form-container">

	<form action="resetPassword" method="post">
        <table>
            <caption>Password Reset</caption>

            <c:forEach items="${USERS}" var="user">
                <tr>
                    <td>
                        <c:out value="${user.username}" />
                    </td>
                    <td>
                        <input type="checkbox" name="username" value="${user.username}" />
                    </td>
                </tr>
            </c:forEach>
        </table>

            <button type="submit">Reset Passwords</button>
    </form>

    <a class="button" href="admin">Back to Santa Admin</a>

</section>
