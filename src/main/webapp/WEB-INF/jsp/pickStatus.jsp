<!-- #include file = "components/setup.asp" -->
<!-- #include file = "components/authenticate.asp" -->
<!-- #include file = "components/header.asp" -->

<%
  'Declare local variables
  Dim pickers, i
%>

<aside id="image-container">
    <img border="0" src="images/SantaRunning2.gif" alt="Santa Running" />
</aside>
        
<section id="form-container">

    <table>

        <caption>Secret Santa Pick Status</caption>

        <%
        SQL = "    SELECT DisplayName, Recipient, Assigned " + _
                "      FROM [recipient] " + _
                "INNER JOIN [user] ON recipient.UserName = user.UserName " + _
                "     WHERE Year = " + YEAR

        set pickers = Server.CreateObject("ADODB.Recordset")
        pickers.Source = SQL
        pickers.ActiveConnection = conn
        pickers.CursorType = 3 'adOpenStatic
        pickers.Open

        for i = 1 to pickers.RecordCount %>
            <tr>
                <td>
                    <%=pickers("DisplayName")%>
                </td>
                <td>
                    <% if pickers("Recipient") <> "" then %>
                    <input type="checkbox" checked onclick="this.checked=true;"/>
                    <% else %>
                    <input type="checkbox" onclick="this.checked=null">
                    <% end if %>
                </td>
            </tr>
            <% pickers.MoveNext
        next
        %>
    </table>

    <form>
        <button formaction="ui_SantaHome.asp">Back to Santa Home</button>
    </form>

</section>

<!-- #include file = "components/footer.asp" -->

<%
    'clean up
    pickers.close
    Set pickers = Nothing
%>