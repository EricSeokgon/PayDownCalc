var counter = new Date().getTime();; //for unique IDs
var triggerclick = false;
uniqueId = function(){
    return 'myid-' + counter++
}

function calcPMI(originalloanamount, currentlyowed, loanyears) {
    var loan2val = Math.round(currentlyowed / originalloanamount * 100);
    
    var pmi_pcntHigh = 0;
    var pmi_pcntLow = 0;
    switch(loan2val) {
        case 100:
        case 99:
        case 98:
        case 97:
        case 96:
        case 95:
            pmi_pcntHigh = 0.9;
            pmi_pcntLow = 0.79;
            break;
        case 94:
        case 93:
        case 92:
        case 91:
        case 90:
            pmi_pcntHigh = 0.78;
            pmi_pcntLow = 0.26;
            break;
        case 88:
        case 87:
        case 86:
        case 85:
            pmi_pcntHigh = 0.52;
            pmi_pcntLow = 0.23;
            break;
        case 84:
        case 83:
        case 82:
        case 81:
            pmi_pcntHigh = 0.32;
            pmi_pcntLow = 0.19;
            break;
            
        default:
            pmi_pcntHigh = 0;
            pmi_pcntLow = 0;
            break;
    }
    
    var pmi_pcnt = pmi_pcntHigh;
    if(loanyears <= 15) {
        pmi_pcnt = pmi_pcntLow;
    } 
    
    return originalloanamount * pmi_pcnt / 100;
    
}

function saveToCookie() {
    
    $("input, select").each(function( index ) {
        $.cookie($(this).attr("id"), $( this ).val(), { expires: 30, path: '/' });
    });
    $.cookie("extrapaymentlogger", $("#extrapaymentlogger" ).html(), { expires: 30, path: '/' });
    
}

function restoreFromCookie() {
    
    var cookiesObj;
    cookiesObj = $.cookie();
   
    var shortlink = getUrlParameter("s");
    
    if(shortlink != undefined) {
        $.ajax({
          type: 'POST',
          url: window.location,
          data: {recalllink: shortlink},
          success: function(data) {
            try {
                cookiesObj = JSON.parse(data); 
                triggerclick = true;

            } catch (e) {
                console.error("Parsing error:", e); 
            }    

          },
          async:false
        });
        
    }
    
    
    var thecookie = {};
    if(cookiesObj.length > 0) {
        thecookie = cookiesObj[0];
    }
    console.log(thecookie);
    console.log(cookiesObj);
    
    for (var index in cookiesObj) {
        var value = cookiesObj[index]
        console.log(index, value);
        if($("#"+index).attr("type") == "checkbox") {
            if(value == "on") {
                $("#"+index).prop("checked", true);
            } else {
                $("#"+index).prop("checked", false)
            }
        } else {
            if(index == "extrapaymentlogger") {
                $("#extrapaymentlogger" ).html(value);
                activateHovers();    
            } else {
                $("#"+index).val(value );
            }
        }
    }
    
}


function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return sParameterName[1];
        }
    }
}

function getPathFromUrl(url) {
  return url.split("?")[0];
}


function genLink () {
    var cookiesObj = JSON.stringify($.cookie());
    $.post( window.location, 
        {genlink: cookiesObj}
        )
        .done(function( data ) {
            var url =getPathFromUrl(window.location.href);
            url = url.replace("#", "");
   
            
            $("#genLinkParent").html(url + "?s="+data);
    });
}


function getMonthText(index) {
    return Months[index];
}
function getMonthInt(text) {
    var date = new Date(text + "8,  2014");
    return date.getMonth();
}

function deletePayment(node) {
    $("#"+node).fadeOut().remove("#"+node);
}


function activateHovers()
{
	//hover states on the static widgets
	$('.fg-button-icon-solo, .fg-button, #androidapp ').hover(
		function() { $(this).addClass('ui-state-hover'); }, 
		function() { $(this).removeClass('ui-state-hover'); }
	);

	$('.uibutton').button();
        $(".ui-state-focus").removeClass("ui-state-focus");

}



function editPayment(node) {
    console.log(node);

    var startmonth = $("#"+node+"startmonth").text();
    var startyear = $("#"+node+"startyear").text();
    var type = $("#"+node+"type").text();
    var amount = $("#"+node+"amount").text();
    var refipcnt = $("#"+node+"refipcnt").text();
    var refilength = $("#"+node+"refilength").text();
    
    if(refipcnt == "") {
        refipcnt = "4.00%";
    }
    if(refilength == "") {
        refilength = "30";
    }
    if(amount == "") {
        amount = "$100.00";
    }
    
    
//    console.log(start, type, amount);
    
    $("#prepaymonthadjust").val(startmonth);
    $("#prepayyearadjust").val(startyear);
    $("#prepaymenttypeadjust").val(type);
    $("#prepayamountadjust").val(amount);
    $("#prepayamountadjust").attr("data-sourceid", node);

    $("#refipercentadjust").val(refipcnt);
    $("#yearrefiadjust").val(refilength);



    if(type == "Refinance") {
        $("#prepayamountadjust").hide();
        $("#divrefiadjust").show();
    } else {
        $("#prepayamountadjust").show();
        $("#divrefiadjust").hide();           
    }

    $("#prepaymentadjust").dialog({
    height: 200,
    width: 550,
    modal:true,
            title: 'Adjust Prepayment',
            hide: 'fade',

    buttons: {
                    "Save": function() { 
                        
                        var node = $("#prepayamountadjust").attr("data-sourceid");
                        $("#"+node+"startmonth").text($("#prepaymonthadjust").val());
                        $("#"+node+"startyear").text($("#prepayyearadjust").val());
                        $("#"+node+"type").text($("#prepaymenttypeadjust").val());

                        if($("#prepaymenttypeadjust").val() == "Refinance") {
                            $("#"+node+"refipcnt").text($("#refipercentadjust").asNumber()+ "%");
                            $("#"+node+"refilength").text($("#yearrefiadjust").val());
                            $("#"+node+"amount").text("");
                            $("#"+node+"spacer").text(" / ");
                            
                        } else {
                            $("#"+node+"amount").text(toMoney($("#prepayamountadjust").val()));
                            $("#"+node+"refipcnt").text("");
                            $("#"+node+"refilength").text("");
                            $("#"+node+"spacer").text("");
                            
                        }
                        

                        $(this).dialog("close"); 
                    }, 
                    "Close": function() { 
                        $(this).dialog("close"); 
                    } 
            }
    });

}

function monthsBetween(dateObj1, dateObj2) {
    var resultObj = new Object();
    resultObj.months = 0;
    resultObj.years = 0;
    if(dateObj1.getMonth() == dateObj2.getMonth()) {
        if(dateObj1.getFullYear() == dateObj2.getFullYear()) {
            return resultObj;
        }   
    }
    
    var testDate1 = new Date(dateObj1.getFullYear(),dateObj1.getMonth(), 8, 12, 0, 0, 0);
    var testDate2 = new Date(dateObj2.getFullYear(),dateObj2.getMonth(), 8, 12, 0, 0, 0);
    var months = 0;
    if(testDate1 > testDate2) {
        while (testDate1 > testDate2) {
            months++;
            testDate2.setMonth(testDate2.getMonth()+1);            
        }
    } else {
        while (testDate2 > testDate1) {
            months++;
            testDate1.setMonth(testDate1.getMonth()+1);            
        }
    }
   
    console.log("months between", months);
    var years = Math.floor(months / 12);
    months = months - (years * 12);
    resultObj.months = months;
    resultObj.years = years;
    
    console.log("Time between", resultObj);
    
    return resultObj;
    
}

function formatDate(dateObj) {
    var year = dateObj.getFullYear();
    var month = getMonthText(dateObj.getMonth());
    return (month +" " + year);
}

function toMoney(mon) {
    var div = $('<input value="'+mon+'"></input>');
    $(div).formatCurrency();
    return $(div).val();
}
function forwardDate() {

    var loanDate = new Date($("#originalstartmonth").val() + " 1, " + $("#originalstartyear").val())
    var nowDate = new Date();
    loanDate.setMonth(loanDate.getMonth()+1);

    if($("#currentlyowed").asNumber() != $("#originalloanamount").asNumber()) {
        if(loanDate < nowDate) {
            
            return true;
        }
        
    }
    
    return false;
}


function showResults(mtg) {
    
    var payoffDateExtras = mtg['date'];
    var payoffDateNoExtra = mtg['datenoextra'];
    
    var template_vars = {
        loanstart: getMonthText(mtg['originalstartmonth']) + " " + mtg['originalstartyear'],
        monthly: toMoney(mtg['originalmonthly']),
        totalinterestpaid: toMoney(mtg['runningtotalinterest']),
        totalpaid: toMoney(mtg['currentlyowed'] + mtg['runningtotalinterest']),
        finalpayoffdate: formatDate(payoffDateExtras)
    };
    
    lineChart = {};
    lineChart.columns = [];
    lineChart.columns[0] = ["number", "Year"];
    lineChart.columns[1] = ["number", "No Extra Payments"];

    if(mtg['prepayments'].length > 0) {
        lineChart.columns[2] = ["number", "Extra Payments"];
    }

    lineChart.push = [];
    
    var loadcount = 0;
    for(var i =0; i<mtg['paymentsnoprepay'].length; i++) {
        var monthtest = mtg['paymentsnoprepay'][i]['date'].getMonth();
        if(monthtest != 1) {
            continue;
        }
        console.log("build no prepay line chart");
        lineChart.push[loadcount] = [];
        lineChart.push[loadcount][0] = mtg['paymentsnoprepay'][i]['date'].getFullYear();
        lineChart.push[loadcount][1] = Math.round(mtg['paymentsnoprepay'][i]['currentlyowed'] 
                                        - mtg['paymentsnoprepay'][i]['runningtotalprincipal']);
        //console.log("lineChart.push", mtg['paymentsnoprepay'][i]['date'], lineChart.push[loadcount]);
        loadcount++;
    }
    

    if(payoffDateExtras > payoffDateNoExtra) {
        console.log("extend line for prepay date exras");
        var yearEnd = payoffDateExtras.getFullYear();
        var yearStart = payoffDateNoExtra.getFullYear();
        
        for(var j = yearStart; yearStart < yearEnd; yearStart++) {
                lineChart.push[loadcount] = [];
                lineChart.push[loadcount][0] = yearStart;
                lineChart.push[loadcount][1] = 0;
                loadcount++;
        }
        
    }    


    if(mtg['prepayments'].length > 0) {
        for(var i =0; i< lineChart.push.length; i++) {
            var yearEntry = lineChart.push[i][0];
            lineChart.push[i][2] = 0;
            for(var j = 0; j < mtg['payments'].length; j++) {
                var yearPrepay = mtg['payments'][j]['date'].getFullYear();
                if(yearPrepay == yearEntry) {
                    lineChart.push[i][2] = Math.round(mtg['payments'][j]['currentlyowed'] 
                                - mtg['payments'][j]['runningtotalprincipal']);
                }

            }

        }
    }
    
    

    /*
    loadcount = 0;
    var previousyear = -1;
    for(var i =0; i<mtg['payments'].length && mtg['prepayments'].length > 0; i++) {
        var monthtest = mtg['payments'][i]['date'].getMonth();
        var yeartest = mtg['payments'][i]['date'].getFullYear();
        if(monthtest != 1 ) {
            continue;
        }
        if(lineChart.push[loadcount][0] != yeartest) {
            continue;
        }
        console.log("build prepay line chart", loadcount);
        lineChart.push[loadcount][2] = Math.round(mtg['currentlyowed'] - mtg['payments'][i]['runningtotalprincipal']);
        console.log("lineChart.push", mtg['payments'][i]['date'], lineChart.push[loadcount]);
        previousyear = previousyear;
        loadcount++;
    }
    */

    
    console.log("linechart rebuild", lineChart);
    //return;
    
    barCharts = [];
    barCharts[0] = [];
    barCharts[0][0] = "Mortgage";

    barCharts[0][3] = 0;
            

    if(mtg['prepayments'].length > 0) {
        console.log(payoffDateExtras, payoffDateNoExtra);
        var timeDiff = monthsBetween(payoffDateExtras, payoffDateNoExtra);
        template_vars.totaltimesaved = timeDiff.years + " years, " + timeDiff.months + " months";
        template_vars.totalinterestsaved = toMoney(Math.abs(mtg['runningtotalinterestnoextra'] - mtg['runningtotalinterest']));
        template_vars.interestsaved = Mustache.render(Templates['interestsaved'], template_vars);

        if(mtg['runningtotalinterestnoextra'] < mtg['runningtotalinterest'] ) {
            template_vars.interestsaved = template_vars.interestsaved.replace("saved", "MORE"); 
        }
        
        template_vars.timesaved = Mustache.render(Templates['timesaved'], template_vars);
        if(payoffDateExtras > payoffDateNoExtra) {
            template_vars.timesaved = template_vars.timesaved.replace("shorter", "LONGER"); 
        }

        barCharts[0][3] = mtg['runningtotalinterestnoextra'] - mtg['runningtotalinterest'];
        
    }

    var resultshtml = Mustache.render(Templates['resultheader'], template_vars);
    if(forwardDate()) {
        resultshtml = "<i>Amortization begins at today's date</i>" + resultshtml;
    }
    $("#summaryID").html(resultshtml);
    
    if(triggerclick) {
        triggerclick = false;
        $("#genLinkParent").html("");
    }
    

    barCharts[0][1] = Math.round(mtg['runningtotalprincipal']);
    barCharts[0][2] = Math.round(mtg['runningtotalinterest']);
    
    
    console.log(mtg['runningtotalprincipal'], mtg['runningtotalinterest'], barCharts);
    drawChart();


    var payments = mtg['payments'];
    var rowBuilder = "";
    var rowhighlighting = "";
    for(var i=0;i< payments.length; i++) {
        
        //console.log(payments[i]);
        var monthly = toMoney(payments[i]['monthly']);
        var type = payments[i]['type'];
        //console.log("payment type is", type);
        var amountinterest = toMoney(payments[i]['amountInterest']);
        var amountinteresttotal = toMoney(payments[i]['runningtotalinterest']);
        var dateformatted = formatDate(payments[i]['date']);
        if(type != "monthly") {

            monthly = "";
            amountinterest = "";
            amountinteresttotal = "";

            if(type == "monthlyextra") {
                dateformatted = "Monthly Extra";
            }
            if(type == "annualextra") {
                dateformatted = "Annual Extra";
            }
            if(type == "one-timeextra") {
                dateformatted = "One-time Extra";
            }
            if(type == "refinanceextra") {
                dateformatted = "Loan Refinance";
            }
            if(type == "pmi") {
                dateformatted = "PMI";
                amountinterest = toMoney(payments[i]['amountInterest']);
            }
            
        }
        
        if (i%2) {
            rowhighlighting = " rowhighlighting ";
        } else {
            rowhighlighting = "";
        }

        //var pcntpaid = Math.round(payments[i]['runningtotalprincipal'] / payments[i]['currentlyowed'] * 10000) / 100;
        var pcntpaid = Math.round(10000 - (payments[i]['currentlyowed'] - payments[i]['runningtotalprincipal']) / mtg['originalloanamount'] * 10000) / 100;
 
 
 
        if (i%20 == 0 && i > 10) {
         rowBuilder += Mustache.render(Templates['resulttablehd'], template_vars);
        }
        
        template_vars = {
            rowhighlighting: rowhighlighting,
            type: type,
            date: dateformatted,
            monthly: (monthly),
            principal: toMoney(payments[i]['amountPrincipal']),
            interest: amountinterest,
            totalint: amountinteresttotal,
            totalprint: toMoney(payments[i]['runningtotalprincipal'] + payments[i]['runningtotalinterest'] ),
            stillowed: toMoney(payments[i]['currentlyowed'] - payments[i]['runningtotalprincipal']),
            pcntpaid: pcntpaid + "%"
        };
        
        var row = Mustache.render(Templates['resulttablerow'], template_vars);
        //console.log(row);
        rowBuilder += row;
    
    }
    
    
    template_vars = {
        tablebody: rowBuilder
    };
    
    var finaltable = Mustache.render(Templates['resulttable'], template_vars);
    //alert(finaltable);

    $("#chartsID").html("charts go here");

        
    $("#amortizationID").html(finaltable);
    
    
}
function amortize(mtg){
    
    var pmicheck = $("#pmicheck").is(":checked");
    var refifee = $("#refifee").asNumber();
    var pmiamount = 0; 
    if(pmicheck) {
        pmiamount = calcPMI(mtg['originalloanamount'], mtg['currentlyowed'], mtg['loanlengthyears']) / 12;
    }
    
    var payments = new Array();
    var runningDate = new Date(mtg['originalstartyear'], mtg['originalstartmonth'] + 1, 8, 0, 0, 0, 0);
    if(forwardDate()) {
        runningDate = new Date();
        runningDate.setDate(8);
    }

//    console.log(runningDate);
    var interestcalc = 0;
    for(var i=0; i<5000; i++) {
        interestcalc = (mtg['currentlyowed'] - mtg['runningtotalprincipal']) * (mtg['interestrate'] / 100) / 12;
        interestcalc = Math.round(interestcalc * 100) / 100;
        
        var stillowed = mtg['currentlyowed']  - mtg['runningtotalprincipal'];
        

        var payment = new Object();
        payment['date'] = new Date(runningDate.getFullYear(),runningDate.getMonth(), 8, 12, 0, 0, 0);
        payment['currentlyowed'] = mtg['currentlyowed'];
        payment['amountPrincipal'] = mtg['monthly']- interestcalc;
        payment['monthly'] = mtg['monthly'];
        if(payment['amountPrincipal'] > stillowed ) {
            payment['amountPrincipal'] = stillowed;
        }
        payment['amountInterest'] = interestcalc;       
        mtg['runningtotalprincipal'] += payment['amountPrincipal'];
        mtg['runningtotalinterest'] += payment['amountInterest'];
        payment['runningtotalprincipal'] = mtg['runningtotalprincipal'];
        payment['runningtotalinterest'] = mtg['runningtotalinterest'];
        payment['type'] = "monthly";
        
        payments.push(payment);
        stillowed = mtg['currentlyowed']  - mtg['runningtotalprincipal'];

        //PMI penalty.
//        console.log("pmi?", (mtg['originalloanamount'] - stillowed) / mtg['originalloanamount']);
        if((((mtg['originalloanamount'] - stillowed) / mtg['originalloanamount']) * 100) < 20 && pmicheck) {
            var payment = new Object();
            payment['date'] = new Date(runningDate.getFullYear(),runningDate.getMonth(), 8, 12, 0, 0, 0);
            payment['currentlyowed'] = mtg['currentlyowed'];
            payment['amountPrincipal'] = 0;
            payment['monthly'] = 0;
            payment['amountInterest'] = pmiamount;       
            mtg['runningtotalinterest'] += payment['amountInterest'];
            payment['runningtotalprincipal'] = mtg['runningtotalprincipal'];
            payment['runningtotalinterest'] = mtg['runningtotalinterest'];
            payment['type'] = "pmi";
            console.log("push pmi");

            payments.push(payment);
            
        }
        
        
 
        for(var j=0; j<mtg['prepayments'].length && stillowed > 0; j++) {
            var monthtest = mtg['prepayments'][j]['startmonth'];
            var yeartest = mtg['prepayments'][j]['startyear'];
            var typetest = mtg['prepayments'][j]['type'];
            var datetest = new Date(monthtest + " 1, " + yeartest);
            var prepayamount = mtg['prepayments'][j]['amount'];
            if(prepayamount > stillowed ) {
                prepayamount = stillowed ;
            }


            if((datetest < runningDate)
                    ) {
                //console.log("Need to apply prepayment", prepayamount, "of", mtg['prepayments'].length, typetest);
                //push prepayment to next time
                payment = new Object();
                payment['date'] = new Date(runningDate.getFullYear(),runningDate.getMonth(), 8, 12, 0, 0, 0);
                payment['currentlyowed'] = mtg['currentlyowed'];
                payment['amountPrincipal'] = prepayamount;
                payment['monthly'] = 0;
                payment['amountInterest'] = 0;       
                payment['runningtotalprincipal'] = mtg['runningtotalprincipal'];
                payment['runningtotalinterest'] = mtg['runningtotalinterest'];
                payment['type'] = typetest.toLowerCase() + "extra";
                
                if(payment['type'] == "monthlyextra") {
                    //console.log("prepayments", "do month");
                    mtg['runningtotalprincipal'] += prepayamount;
                    datetest = new Date(runningDate.getFullYear(),runningDate.getMonth()+1, 1, 12, 0, 0, 0);
                    mtg['prepayments'][j]['startmonth'] = getMonthText(datetest.getMonth());
                    mtg['prepayments'][j]['startyear'] = datetest.getFullYear();
                    payments.push(payment);
                }
                if(payment['type'] == "annualextra" &&
                        datetest.getMonth() == runningDate.getMonth()) {
                    //console.log("prepayments", "do annual");
                    mtg['runningtotalprincipal'] += prepayamount;
                    datetest = new Date(runningDate.getFullYear()+1,runningDate.getMonth(), 1, 12, 0, 0, 0);
                    mtg['prepayments'][j]['startmonth'] = getMonthText(datetest.getMonth());
                    mtg['prepayments'][j]['startyear'] = datetest.getFullYear();
                    payments.push(payment);
                }
                if(payment['type'] == "one-timeextra"&&
                        datetest.getMonth() == runningDate.getMonth()) {
                    //console.log("prepayments", "do one-time");
                    mtg['runningtotalprincipal'] += prepayamount;
                    datetest = new Date(runningDate.getFullYear()+1,runningDate.getMonth(), 1, 12, 0, 0, 0);
                    mtg['prepayments'][j]['startmonth'] = getMonthText(datetest.getMonth());
                    mtg['prepayments'][j]['startyear'] = 9999;
                    payments.push(payment);
                }
                if(payment['type'] == "refinanceextra"&&
                        datetest.getMonth() == runningDate.getMonth()) {
                    //console.log("prepayments", "do refinance");
                    payment['amountPrincipal'] = 0 - refifee;
                    payment['runningtotalprincipal'] = 0;
                    payment['runningtotalinterest'] = mtg['runningtotalinterest'];
                    datetest = new Date(runningDate.getFullYear()+1,runningDate.getMonth(), 1, 12, 0, 0, 0);
                    mtg['prepayments'][j]['startmonth'] = getMonthText(datetest.getMonth());
                    mtg['prepayments'][j]['startyear'] = 9999;
                    
                    var mtgObj = mtgCalc(stillowed+refifee, mtg['prepayments'][j]['refipcnt'], 
                                mtg['prepayments'][j]['refilength']);
 
                    console.log("old monthyly was", mtg['monthly'], "now it is ",mtgObj['monthly']);
                    mtg['currentlyowed']  = stillowed+refifee;
                    payment['currentlyowed'] = mtg['currentlyowed'];
                    mtg['monthly'] = mtgObj['monthly'];
                    mtg['interestrate'] = mtgObj['interestrate'];
                    mtg['runningtotalprincipal'] = 0;
                    mtg['loanlengthyears']= mtgObj['loanlengthyears'];
                    pmiamount = calcPMI(mtg['originalloanamount'], mtg['currentlyowed'], mtg['loanlengthyears'])  / 12;

                    payments.push(payment);
                }
                

            }
            
            stillowed = mtg['currentlyowed']  - mtg['runningtotalprincipal'];
   
        }

        
        if((mtg['currentlyowed']  - mtg['runningtotalprincipal'] ) <= 0) {
            break;
        }
        
        runningDate.setMonth(runningDate.getMonth()+1);
  //      console.log(runningDate);
    }
    
    return payments;

    
    
}


var trackOutboundLink = function(url) {
   ga('send', 'event', 'outbound', 'click', url, {'hitCallback':
     function () {
     //callback
     }
   });
}

function mtgCalc(originalloanamount, interestrate, loanlengthyears) {
    
    
    var nummonths = loanlengthyears*12;
    var monthlyinterest = (interestrate / (12 * 100));
    var denominator = 1  - Math.pow( 1 + monthlyinterest, 0-nummonths);
    var monthlypaymentraw = (originalloanamount) * (monthlyinterest / denominator);
    
    var totalinterestraw = (monthlypaymentraw * nummonths) - originalloanamount;
    var totalinterestmonthlyraw = totalinterestraw / nummonths;
    var totalamountraw = totalinterestraw + originalloanamount;
    
    
    var mtg = new Object();
    mtg['originalloanamount'] = Math.round(originalloanamount * 100) / 100;
    mtg['interestrate'] =Math.round(interestrate *10000) / 10000;
    mtg['loanlengthyears'] = loanlengthyears;
    mtg['monthly'] = Math.round(monthlypaymentraw* 100) / 100;
    mtg['totalinterest'] = Math.round(totalinterestraw* 100) / 100;
    mtg['totalpaid'] = Math.round(totalamountraw* 100) / 100;
    mtg['runningtotalprincipal'] = 0;
    mtg['runningtotalinterest'] = 0;
    
    return mtg;	
        
    
}

//ready()
$(function() {
     
    restoreFromCookie();
    
    
    $( "#showadvanced" ).click(function() {

        $( ".advanced" ).slideToggle( "fast", function() {
            
            if($( ".advanced" ).is(":visible")) {
                $( "#showadvanced" ).text("Hide Advanced");
            } else {
                $( "#showadvanced" ).text("Show Advanced");
            }

        });
    });

    


    //load templates.		
    /*
    $.get( "templates/resultheader.html", function( data ) {
        Templates['resultheader'] = data;
    });
    */

    //initCharts();

    $("#addprepayment").click(function(){

        template_vars = {
            startmonth: $("#extrapaymentmonth").val(),
            startyear: $("#extrapaymentyear").val(),
            type: $("#prepaymenttype").val(),
            amount: toMoney($("#prepayamount").val()),
            id: uniqueId()
                    
        };


        if($("#prepaymenttype").val() == "Refinance") {
            template_vars.refipcnt = $("#refipercent").asNumber()+ "%";
            template_vars.spacer = " / ";
            template_vars.refilength =  $("#yearrefi").val();
            template_vars.amount =  "";
//            template_vars.amount = ($("#refipercent").val() + " / " + $("#yearrefi").val() + " Years");
        }


        var resultshtml = Mustache.render(Templates['prepaymentrow'], template_vars);
        $("#extrapaymentlogger").html(resultshtml + $("#extrapaymentlogger").html());

        activateHovers();    
    });


    $( "#addprepayment, .ui-button-text" ).button();
 
    $("#prepaymenttype").change(function() {
       if($(this).val() == "Refinance") {
           $("#prepayamount").hide();
           $("#divrefi").show();
       } else {
           $("#prepayamount").show();
           $("#divrefi").hide();           
       }
    });
 
    $("#prepaymenttypeadjust").change(function() {

        if($(this).val() == "Refinance") {
            $("#prepayamountadjust").hide();
            $("#divrefiadjust").show();
        } else {
            $("#prepayamountadjust").show();
            $("#divrefiadjust").hide();           
        }

    });
    
    $("#prepaymenttype").trigger("change");
    $("#prepaymenttypeadjust").trigger("change");
 
 
 
    $('#docalculate').click(function() {
        
        saveToCookie();
 
        $("#summaryID").html("<center><img src='ajax-loader.gif' ></center>");
        $("#PvIchart_div").html("");
        $("#mtgAmortizeLine").html("");
        $("#amortizationID").html("");
        trackOutboundLink('Amortize');
//        _gaq.push(['_trackEvent', 'External Link', 'Twitter Link', 'Follow Us - Words']);
        
        window.setTimeout(function() {

            var originalloanamount = $('#originalloanamount').asNumber();
            var currentlyowed = $('#currentlyowed').asNumber();
            var interestrate = $('#interestrate').asNumber();
            var loanlength = $('#loanlength').asNumber();
            $("#resultsID").html(originalloanamount + 
                                 " " +currentlyowed +
                                 " " +interestrate +
                                 " " +loanlength +
                                 " " );

            var mtgObj = mtgCalc(originalloanamount, interestrate, loanlength);
            var prepaypayments = [];
            mtgObj['originalmonthly'] = mtgObj['monthly'];
            mtgObj['originalstartmonth'] = getMonthInt($('#originalstartmonth').val());
            mtgObj['originalstartyear'] = $('#originalstartyear').val();
            mtgObj['currentlyowed'] = currentlyowed;
            mtgObj['prepayments'] = [];
            mtgObj['paymentsnoprepay'] = amortize(mtgObj);

            mtgObj['runningtotalprincipalnoextra'] = mtgObj['runningtotalprincipal'];
            mtgObj['runningtotalinterestnoextra'] = mtgObj['runningtotalinterest'];
            var paymentsnum = mtgObj['paymentsnoprepay'].length;
            mtgObj['datenoextra'] = mtgObj['paymentsnoprepay'][(paymentsnum - 1)]['date'];

            var prepaytable = $(".prepaytable");
            mtgObj['prepaycount'] = prepaytable.length;
            $($(prepaytable)).each(function() {
                mtgObj['runningtotalprincipal'] = 0;
                mtgObj['runningtotalinterest'] = 0;
                var prepayObj = new Object();
                var myid = $(this).attr("id");
                prepayObj.id = myid;
                prepayObj.startmonth = $("#"+myid + "startmonth").text();
                prepayObj.startyear = $("#"+myid + "startyear").text();
                prepayObj.type = $("#"+myid + "type").text();
                prepayObj.amount = $("#"+myid + "amount").asNumber();
                prepayObj.refipcnt = $("#"+myid + "refipcnt").asNumber();
                prepayObj.refilength = $("#"+myid + "refilength").text();
                prepaypayments.push(prepayObj);
            });
            console.log(prepaypayments);
            mtgObj['prepayments'] = prepaypayments;
            //mtgObj['prepayments'] =  prepayObj;
            if(prepaytable.length > 0) {
                mtgObj['paymentswithprepay'] =  amortize(mtgObj);
            } else {
                mtgObj['paymentswithprepay'] =  mtgObj['paymentsnoprepay'];
            }

            paymentsnum = mtgObj['paymentswithprepay'].length;
            mtgObj['date'] = mtgObj['paymentswithprepay'][(paymentsnum - 1)]['date'];
            mtgObj['payments'] = mtgObj['paymentswithprepay'];

            showResults(mtgObj);
            $(".ui-state-focus").removeClass("ui-state-focus");
             
        }
        , 500);


        return false;


    });    


    $(".currency").blur(function(){
        $(this).formatCurrency();
        return false;
    });


    // Hover states on the static widgets
    $( "#dialog-link, #icons li" ).hover(
        function() {
                $( this ).addClass( "ui-state-hover" );
        },
        function() {
                $( this ).removeClass( "ui-state-hover" );
        }
    );
    
    if(triggerclick) {
        console.log("tigger click");
        $('#docalculate').trigger("click");
    }

    
    
});
