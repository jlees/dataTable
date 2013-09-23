$(document).ready(function() {
    $('#contacts_table').dataTable( {
        "bProcessing": true,
        "aaSorting": [[ 0, "asc" ]],
        "bServerSide": true,
        "bFilter":false,
        "bJQueryUI": true,
        "sPaginationType": "full_numbers",
        "sAjaxSource": loadContactDataTableRoute
    } );
} );
