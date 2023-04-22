<%--
  ~ Copyright (c) 2016 Google Inc. All Rights Reserved.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License"); you
  ~ may not use this file except in compliance with the License. You may
  ~ obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
  ~ implied. See the License for the specific language governing
  ~ permissions and limitations under the License.
  --%>
<%@ page contentType='text/html;charset=UTF-8' language='java' %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core'%>

<html>
  <%@ include file="header.jsp" %>
  <link type='text/css' href='/css/show_test_runs_common.css' rel='stylesheet'>
  <link type='text/css' href='/css/test_results.css' rel='stylesheet'>
  <link rel='stylesheet' href='/css/search_header.css'>
  <script type='text/javascript' src='https://www.gstatic.com/charts/loader.js'></script>
  <script src='https://www.gstatic.com/external_hosted/moment/min/moment-with-locales.min.js'></script>
  <script src='js/search_header.js'></script>
  <script src='js/time.js'></script>
  <script src='js/test_results.js'></script>
  <script type='text/javascript'>
      google.charts.load('current', {'packages':['table', 'corechart']});
      google.charts.setOnLoadCallback(drawPieChart);

      var search;

      $(document).ready(function() {
          search = $('#filter-bar').createSearchHeader('Module: ', '${testName}', refresh);
          search.addFilter('Branch', 'branch', {
            corpus: ${branches}
          }, ${branch});
          search.addFilter('Device', 'device', {
            corpus: ${devices}
          }, ${device});
          search.addFilter('Device Build ID', 'deviceBuildId', {}, ${deviceBuildId});
          search.addFilter('Test Build ID', 'testBuildId', {}, ${testBuildId});
          search.addFilter('Host', 'hostname', {}, ${hostname});
          search.addFilter('Passing Count', 'passing', {
            validate: 'inequality',
            width: 's2'
          }, ${passing});
          search.addFilter('Non-Passing Count', 'nonpassing', {
            validate: 'inequality',
            width: 's2'
          }, ${nonpassing});
          search.addRunTypeCheckboxes(${showPresubmit}, ${showPostsubmit});
          search.display();

          // disable buttons on load
          if (!${hasNewer}) {
              $('#newer-button').toggleClass('disabled');
          }
          if (!${hasOlder}) {
              $('#older-button').toggleClass('disabled');
          }
          $('#tableLink').click(function() {
              window.open('/show_table?testName=${testName}&treeDefault=false', '_self');
          });
          $('#newer-button').click(prev);
          $('#older-button').click(next);
          $('#test-results-container').showTests(${testRuns});

          $('#apiCoverageModal').modal({
                  width: '75%',
                  dismissible: true, // Modal can be dismissed by clicking outside of the modal
                  opacity: .5, // Opacity of modal background
                  inDuration: 300, // Transition in duration
                  outDuration: 200, // Transition out duration
                  startingTop: '4%', // Starting top style attribute
                  endingTop: '10%', // Ending top style attribute
                  ready: function(modal, trigger) { // Callback for Modal open. Modal and trigger parameters available.
                      var urlSafeKeyList = modal.data('urlSafeKeyList');
                      var halApiInfoList = [];
                      var getAjaxList = $.map( urlSafeKeyList, function( urlSafeKey ) {
                          return $.get( "/api/coverage/api/data?key=" + urlSafeKey, function(data) {
                                  halApiInfoList.push(data);
                              })
                              .fail(function() {
                                  alert( "Error : can't bring API coverage data from the server" );
                              });
                      });

                      $.when.apply($, getAjaxList).then(function() {
                          $.each(halApiInfoList, function( index, data ) {
                              $("#halApiList").append(halApiListTemplate());
                              var version = data.halMajorVersion + '.' + data.halMinorVersion;
                              var defaultInfo = data.halPackageName + '@' + version + '::' + data.halInterfaceName;
                              $("#halApiList > li:last > div.collapsible-header").html(
                                  '<i class="material-icons">report</i> HAL API Information : ' + defaultInfo
                              );

                              $("#halApiList > li:last > div.collapsible-body > ul.collection.with-header").append(
                                  $.map( data.halApi, function( apiName, idx ) {
                                      var colorClass = data.coveredHalApi.indexOf(apiName) > -1 ? "green" : "red"
                                      return '<li class="collection-item ' + colorClass + ' lighten-1">' + apiName + '</li>';
                                  }).join("")
                              );
                              $("#halApiList").collapsible('open', index);
                          });
                          $('#dataTableLoading').hide("slow");
                      });
                  },
                  complete: function() {
                      $("#halApiList").empty();
                      $('#dataTableLoading').show("slow");
                  } // Callback for Modal close
              }
          );
      });

      function halApiListTemplate() {
          return '<li>' +
              '<div class="collapsible-header">' +
              '<i class="material-icons">report</i> API Information' +
              '</div>' +
              '<div class="collapsible-body">' +
              '<ul class="collection with-header">' +
              '</ul>' +
              '</div>' +
              '</li>';
      }

      // refresh the page to see the selected test types (pre-/post-submit)
      function refresh() {
          if($(this).hasClass('disabled')) return;
          var link = '${pageContext.request.contextPath}' +
              '/show_tree?testName=${testName}' + search.args();
          if (${unfiltered}) {
            link += '&unfiltered=';
          }
          window.open(link,'_self');
      }

      // view older data
      function next() {
          if($(this).hasClass('disabled')) return;
          var endTime = ${startTime};
          var link = '${pageContext.request.contextPath}' +
              '/show_tree?testName=${testName}&endTime=' + endTime +
              search.args();
          if (${unfiltered}) {
              link += '&unfiltered=';
          }
          window.open(link,'_self');
      }

      // view newer data
      function prev() {
          if($(this).hasClass('disabled')) return;
          var startTime = ${endTime};
          var link = '${pageContext.request.contextPath}' +
              '/show_tree?testName=${testName}&startTime=' + startTime +
              search.args();
          if (${unfiltered}) {
              link += '&unfiltered=';
          }
          window.open(link,'_self');
        }

      // to draw pie chart
      function drawPieChart() {
          var topBuildResultCounts = ${topBuildResultCounts};
          if (topBuildResultCounts.length < 1) {
              return;
          }
          var resultNames = ${resultNamesJson};
          var rows = resultNames.map(function(res, i) {
              nickname = res.replace('TEST_CASE_RESULT_', '').replace('_', ' ')
                         .trim().toLowerCase();
              return [nickname, parseInt(topBuildResultCounts[i])];
          });
          rows.unshift(['Result', 'Count']);

          // Get CSS color definitions (or default to white)
          var colors = resultNames.map(function(res) {
              return $('.' + res).css('background-color') || 'white';
          });

          var data = google.visualization.arrayToDataTable(rows);
          var options = {
              is3D: false,
              colors: colors,
              fontName: 'Roboto',
              fontSize: '14px',
              legend: {position: 'bottom'},
              tooltip: {showColorCode: true, ignoreBounds: false},
              chartArea: {height: '80%', width: '90%'},
              pieHole: 0.4
          };

          var chart = new google.visualization.PieChart(document.getElementById('pie-chart-div'));
          chart.draw(data, options);
      }
  </script>

  <body>
    <div class='wide container'>
      <div class='row'>
        <div class='col s12'>
          <div class='card'>
            <ul class='tabs'>
              <li class='tab col s6'><a class='active'>Tree</a></li>
              <li class='tab col s6' id='tableLink'><a>Table</a></li>
            </ul>
          </div>
          <div id='filter-bar'></div>
        </div>
        <div class='col s7'>
          <div class='col s12 card center-align'>
            <div id='legend-wrapper'>
              <c:forEach items='${resultNames}' var='res'>
                <div class='center-align legend-entry'>
                  <c:set var='trimmed' value='${fn:replace(res, "TEST_CASE_RESULT_", "")}'/>
                  <c:set var='nickname' value='${fn:replace(trimmed, "_", " ")}'/>
                  <label for='${res}'>${nickname}</label>
                  <div id='${res}' class='${res} legend-bubble'></div>
                </div>
              </c:forEach>
            </div>
          </div>
          <div id='profiling-container' class='col s12'>
            <c:choose>
              <c:when test='${empty profilingPointNames}'>
                <div id='error-div' class='center-align card'><h5>${error}</h5></div>
              </c:when>
              <c:otherwise>
                <ul id='profiling-body' class='collapsible' data-collapsible='accordion'>
                  <li>
                    <div class='collapsible-header'><i class='material-icons'>timeline</i>Profiling Graphs</div>
                    <div class='collapsible-body'>
                      <ul id='profiling-list' class='collection'>
                        <c:forEach items='${profilingPointNames}' var='pt'>
                          <c:set var='profPointArgs' value='testName=${testName}&profilingPoint=${pt}'/>
                          <c:set var='timeArgs' value='endTime=${endTime}'/>
                          <a href='/show_graph?${profPointArgs}&${timeArgs}'
                             class='collection-item profiling-point-name'>${pt}
                          </a>
                        </c:forEach>
                      </ul>
                    </div>
                  </li>
                  <li>
                    <a class='collapsible-link' href='/show_performance_digest?testName=${testName}'>
                      <div class='collapsible-header'><i class='material-icons'>toc</i>Performance Digest</div>
                    </a>
                  </li>
                </ul>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
        <div class='col s5 valign-wrapper'>
          <!-- pie chart -->
          <div id='pie-chart-wrapper' class='col s12 valign center-align card'>
            <h6 class='pie-chart-title'>${topBuildId}</h6>
            <div id='pie-chart-div'></div>
          </div>
        </div>
      </div>

      <div class='col s12' id='test-results-container'>
      </div>
      <div id='newer-wrapper' class='page-button-wrapper fixed-action-btn'>
        <a id='newer-button' class='btn-floating btn red waves-effect'>
          <i class='large material-icons'>keyboard_arrow_left</i>
        </a>
      </div>
      <div id='older-wrapper' class='page-button-wrapper fixed-action-btn'>
        <a id='older-button' class='btn-floating btn red waves-effect'>
          <i class='large material-icons'>keyboard_arrow_right</i>
        </a>
      </div>
    </div>

    <!-- Modal Structure -->
    <div id="apiCoverageModal" class="modal modal-fixed-footer" style="width: 75%;">
      <div class="modal-content">
        <h4 id="coverageModalTitle">API Coverage</h4>

        <div class="preloader-wrapper big active loaders">
          <div id="dataTableLoading" class="spinner-layer spinner-blue-only">
            <div class="circle-clipper left">
              <div class="circle"></div>
            </div>
            <div class="gap-patch">
              <div class="circle"></div>
            </div>
            <div class="circle-clipper right">
              <div class="circle"></div>
            </div>
          </div>
        </div>

        <div class="row">
          <div class="col s12">
            <ul class="collection with-header">
              <li class="collection-header">
                <h4>Total HAL API List</h4>
                <ul id="halApiList" class="collapsible popout" data-collapsible="expandable">

                </ul>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <a href="#!" class="modal-action modal-close waves-effect waves-green btn-flat ">Close</a>
      </div>
    </div>

    <%@ include file="footer.jsp" %>
  </body>
</html>
