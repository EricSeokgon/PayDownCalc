// Load the Visualization API and the piechart package.
google.load('visualization', '1.0', {'packages':['corechart']});

//#
//
// Set a callback to run when the Google Visualization API is loaded.
//google.setOnLoadCallback(drawChart);

// Callback that creates and populates a data table,
// instantiates the pie chart, passes in the data and
// draws it.
function drawChart()
{    
     
  // Create the data table.
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'Number');
  data.addColumn('number', 'Principal');
  data.addColumn('number', 'Int/Fees');
  data.addColumn('number', 'Savings');

  data.addRows(barCharts);
  console.log("Google barCharts", barCharts);


  var formatter = new google.visualization.NumberFormat(
      {prefix: '$', negativeColor: 'red', negativeParens: true});
  formatter.format(data, 1); // Apply formatter to second column
  formatter.format(data, 2); // Apply formatter to second column
  formatter.format(data, 3); // Apply formatter to second column

/*
  data.addRows([
    ['1', 5, 10, 20],
    ['2', 52, 102, 202]
  ]
               );
*/
  // Set chart options
  var options = {'title':'Principal versus Interest',
                    'width':400,
                    'height':300,
                    'is3D':true,
                    backgroundColor: '#FCFDFD',

                    hAxis: {format: '$#,###'},
                    vAxis: {format: '$#,###',minValue: 0} 
                };

  // Instantiate and draw our chart, passing in some options.
  var chart = new google.visualization.ColumnChart(document.getElementById('PvIchart_div'));
  chart.draw(data, options);
  
 var dataLine = new google.visualization.DataTable();
 
 for(i = 0; i < lineChart['columns'].length; i++)
 {
     dataLine.addColumn( lineChart['columns'][i][0] + '',lineChart['columns'][i][1]  + '');
 }
 dataLine.addRows(lineChart['push']);

 var optionsLine = {
    width: 500, height: 300, backgroundColor: '#FCFDFD',

    title: 'Amortization',
        hAxis: {format: '####'},
        vAxis: {format: '$#,###'} 
    };

    formatter.format(dataLine, 1);
    console.log("Google lineChart", lineChart);
    if(lineChart['columns'].length > 2) {

        formatter.format(dataLine, 2); 
    }

 var chartLine = new google.visualization.LineChart(document.getElementById('mtgAmortizeLine'));
 chartLine.draw(dataLine, optionsLine);
  
}