<?php
	require_once "sc_header.php";
	require_once "sc_menubar.php";
?>
    </td></tr>
    <form name='signup_form' class='formNoLine' method='post' action='sc_URL_addCheck.php'>
    <tr><td class='thGrayC' colspan='7' style='letter-spacing: 8px;'>
        <b>URL 추가</b>
    </td></tr>
    <tr height='30px'><td class='tdGrayCH' colspan='2' style='letter-spacing: 2px;'>
        <label for='add_block'><b>분류</b></label>
    </td><td class='tdLeftU' colspan='5' style='padding: 7px 0px 7px 15px;'>
		<select id='add_block' name='add_block'>"
			<option style='color: #EE0022;' value='0'>차단</option>
			<option style='color: #40C355;' value='1'>허가</option>
			<option style='color: #4E9FD8;' value='2' selected>안전</option>
		</select>
    </td></tr>
    <tr height='30px'><td class='tdGrayCH' colspan='2' style='letter-spacing: 4px;'>
        <label for='add_URL'><b>URL</b></label>
    </td><td class='tdLeftU' colspan='5' style='padding: 7px 0px 7px 15px;'>
        <input type='text' id='add_URL' name='add_URL' class='inputNanum' style='width: 1200px;' placeholder='U R L' required>
    </td></tr>
    <tr><td class='tdRightU' colspan='7' style='padding: 10px 30px;'>
        <input type='submit' id='submit' class='butWhiteH' value='추가' disabled>
        <button type='button' class='butWhiteH' onclick="history.go(-1)">취소</button>
    </td></tr>
    </form>
    </table>

<script type="text/javascript">
    $('input').keyup(function(){
        var objURL = $('input#add_URL').val();

        if(objURL!=""){
            $('input#submit').attr('disabled', false);
        }else{
            $('input#submit').attr('disabled', true);
        }
    });
</script>
<?php
	require_once "sc_footer.php";
?>
