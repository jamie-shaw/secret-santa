<!-- #include file = "components/setup.asp" -->
<!-- #include file = "components/authenticate.asp" -->
<!-- #include file = "components/header.asp" -->

<%
  'Declare local variables
  Dim  recipient, Gifts, i

  'Get recipient for user
  SQL = "SELECT Recipient " + _
        "  FROM [recipient] " + _
        " WHERE username = '" + CurrentUser + "' AND Year = " + YEAR
  set recipient = conn.execute(SQL)
%>

<aside id="image-container">
    <img border="0" src="images/SantaPointing2.gif" alt="Santa" />
</aside>
        
<section id="form-container">

    <h1><%=recipient("Recipient")%>'s Ideas for Santa</h1>

    <table>
        <%
        SQL = "SELECT GiftId, Description " + _
                "FROM gift " + _
                "WHERE UserName = '" & recipient("Recipient") + "' AND Year = " + YEAR

        set gifts = Server.CreateObject("ADODB.Recordset")
        gifts.Source = SQL
        gifts.ActiveConnection = conn
        gifts.CursorType = 3 'adOpenStatic
        gifts.Open

        if gifts.RecordCount > 0 then
            for i = 1 to gifts.RecordCount %>
                <tr>
                    <td>
                        <%=gifts("Description")%>
                    </td>
                </tr>
                <% gifts.MoveNext
            next
        else %>
            <tr>
                <td class="align-center">
                    No suggestions have been made yet.
                </td>
            </tr>
        <% end if %>
    </table>
    
    <form >
        <button formaction="ui_SantaHome.asp">Back to Santa Home</button>
    </form>

</section>

<%
    'clean up
    recipient.close
    Set recipient = Nothing
    
    gifts.close
    Set gifts = Nothing

    conn.close
    Set conn = Nothing
%>

<!-- #include file = "components/footer.asp" -->