<!-- #include file = "components/setup.asp" --> 
<!-- #include file = "components/authenticate.asp" -->
<!-- #include file = "components/header.asp" -->

<%
    'Declare local variables
    Dim emailTo, emailMessage, xmlHttp, url, args, ajaxResponse, recipientRS, emailRS

    'Get recipient for user
    SQL = "SELECT Recipient, Email " + _
            "FROM [recipient] INNER JOIN [user] ON recipient.Recipient = user.UserName " + _
           "WHERE recipient.UserName = '" + CurrentUser + "' AND Year = " + YEAR

    set recipientRS = conn.execute(SQL)

    if Request.Form("message") <> "" then
        if Request.Form("to") = "recipient" then 
            emailTo = recipientRS("Email")
        else 
            SQL = "SELECT Email " + _
                    "FROM recipient INNER JOIN [user] ON recipient.UserName = user.UserName " + _
                   "WHERE Recipient = '" + CurrentUser + "' AND Year = " + YEAR

            set emailRS = conn.execute(SQL)
      
            emailTo = emailRS("Email")
        end if

        emailMessage = Request.Form("message")

        url = "http://www.pmshockey.com/wp-admin/admin-ajax.php?"

        args = "action=send_email"
        args = args + "&to=" + emailTo
        args = args + "&message=" + emailMessage
    
        Set xmlHttp = Server.Createobject("MSXML2.ServerXMLHTTP.6.0")
        xmlHttp.Open "GET", url + args, False

        xmlHttp.setRequestHeader "content-type", "application/x-www-form-urlencoded"

        xmlHttp.Send

        ajaxResponse = xmlHttp.responseText

        xmlHttp.abort()
        set xmlHttp = Nothing  

    end if
%>

<aside id="image-container">
    <img border="0" src="images/SantaPointing2.gif" alt="Santa Pointing" />
</aside>
        
<section id="form-container">

    <% if Request.Form("message") <> "" then %>
   
        <div class="alert alert-success">
            Your message has been sent.
        </div>
    
    <% else %>

        <form name="bodyform" method="post" onSubmit="return validate(this)">
            <fieldset>
                <legend>&nbsp;Send message to:&nbsp;</legend>
                <input type="radio" name="to" value="santa" checked/> My Secret Santa&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="radio" name="to" value="recipient"/> My Recipient (<%=recipientRS("Recipient")%>)<br/>
            </fieldset>
            <br />

            Please enter your message below:
            <br/>
            <textarea name="Message" rows="3" cols="40" maxlength="250" id="Message"></textarea>
            <br/>
        
            <button formaction="ui_SendEmail.asp">Send Message</button>
        </form>
   
     <% end if %>

    <form>
        <button formaction="ui_SantaHome.asp">Back to Santa Home</button>
    </form>
</section>

<script>
    <!--
    function validate(form) {
        if (form.Message.value == '') {
            alert('Please enter a message.');
            return false;
        }

        return true;
    }
    -->
</script>

<!-- #include file = "components/footer.asp" -->
