
Templates = new Object();


Templates['resultheader'] =  ' <table style="width:100%" border="0"> \
    <tbody><tr valign="top"> \
        <td> \
Loan Start: <b>{{loanstart}}</b> \
<br> \
Monthly Payment: <b>{{monthly}}</b><br> \
Interest paid: <b>{{totalinterestpaid}}</b> \
    {{&interestsaved}} \
<br>Final Payoff: <b>{{finalpayoffdate}}</b> \
    {{&timesaved}} \
		 <br>Total Amount Paid: <b>{{totalpaid}}</b> \
        </td> \
        <td> <div id="genLinkParent"> <!-- a id="genLink" href="javascript:genLink()">Generate Share Link</a --></div> \
        </td> \
    </tr> \
</tbody></table> ';


Templates['resulttablehd'] = ' <tr class="ui-state-default " > \
    <th style="width:100px;">Date</th> \
    <th>Monthly</th> \
    <th>Principal</th> \
    <th>Interest/Fees</th> \
    <th>Total Int./Fees</th> \
    <th>Total Pr+Int+Fees.</th> \
    <th>Still Owed</th> \
    <th>% Paid</th> \
</tr>';


Templates['resulttable'] =  '   <div class="ui-grid-header ui-widget-header ui-corner-top">\n\
Amortization</div><table id="amortizationtable" style="width:100%;" class="ui-grid-content ui-widget-content">    \
<tbody>' + Templates['resulttablehd'] + '  \
    {{&tablebody}} \
    </tbody></table>';

Templates['resulttablerow'] = '<tr class="ui-widget-content  {{rowhighlighting}} {{type}}" style="text-align:center;"> \
    <td class="">{{date}}</td> \
    <td class="">{{monthly}}</td> \
    <td class="">{{principal}}</td> \
    <td class="">{{interest}}</td> \
    <td class="">{{totalint}}</td> \
    <td class="">{{totalprint}}</td> \
    <td class="">{{stillowed}}</td> \
    <td class="">{{pcntpaid}}</td> \
</tr>';

var Months = Array("January","February","March","April","May","June","July","August","September","October","November","December");

Templates['interestsaved'] = "<span class='extrasave' style='color:red;'> (<b>{{totalinterestsaved}}</b> saved)</span>";
Templates['timesaved'] = "<span class='extrasave' style='color:red;'> <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<b>{{totaltimesaved}}</b> shorter loan)</span>"

Templates['prepaymentrow'] = "<table id='{{id}}' class='prepaytable' style=\"width: 100%;\" border=0><tr>\n\
    <td><span id='{{id}}startmonth'>{{startmonth}}</span> \n\
        <span id='{{id}}startyear'>{{startyear}}</span></td><td><b id='{{id}}type'>{{type}}</b></td>\n\
        <td > <span id='{{id}}amount'>{{amount}}</span>\n\n\
        <span id='{{id}}refipcnt'>{{refipcnt}}</span><span id='{{id}}spacer'>{{spacer}}</span><span id='{{id}}refilength'>{{refilength}}</span> \
    </td><td><a style='float:right;' href='javascript:deletePayment(\"{{id}}\")' \
    class='fg-button ui-state-default fg-button-icon-solo  ui-corner-all newbutton' title='Delete'> \
        <span class='ui-icon ui-icon-trash'></span> Delete</a> \
    <a style='float:right;' href='javascript:editPayment(\"{{id}}\")' \
    class='fg-button ui-state-default fg-button-icon-solo  ui-corner-all newbutton' title='Edit'> \
        <span class='ui-icon ui-icon-wrench'></span> Edit</a></td></tr></table>";

        //<span id='{{id}}startmonth'>{{startmonth}}
