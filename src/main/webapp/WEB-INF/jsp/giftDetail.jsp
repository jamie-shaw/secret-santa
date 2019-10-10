<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="/images/SantaLaughing2.gif" alt="Santa Laughing" />
</aside>
        
<section id="form-container">
    
    <c:choose>
        <c:when test="${not empty giftId}">
            <form action="gift/detail" method="post" onSubmit="return validate(this)">
                <input type="hidden" name="giftId" value="${GIFT.giftId}" />
                <b>
                    Please edit the description of your gift idea:
                </b>
                <br/>
                <br/>
                <textarea name="Description" rows="3" cols="40" maxlength="250" id="Description">GIFT.description</textarea>
                <br/>
                <button>Save Changes</button>
            </form>
        </c:when>
        <c:otherwise>
            <form action="gift/detail" method="post" onSubmit="return validate(this)">
                <p>
                    <b>Please enter a description of your gift idea:</b>
                    <textarea name="Description" rows="3" cols="40" id="Description"></textarea>
                    <br/>
                    <button>Save Idea</button>
                </p>
            </form>
            <p>
                <a class="button" href="detail">Cancel</a>
            </p>
        </c:otherwise>
    </c:choose>
    
</section>

<script>
    <!--
    function validate(form) {
        if (form.Description.value == '') {
            alert('Please enter a description.');
            return false;
        }

        return true;
    }
    -->
</script>

