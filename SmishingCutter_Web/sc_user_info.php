<?php
    require_once "sc_header.php";
	require_once "sc_DB_conn.php";
	require_once "sc_menubar.php";
?>
		</td>
	</tr>
    <tr>
        <td class='thGrayC' colspan='7' style='letter-spacing: 8px;'><b>내정보</b></td>
    </tr>
    <tr height='30px'>
        <td class='tdGrayCH' colspan='2' style='letter-spacing: 2px;'><b>이름</b></td>
        <td class='tdLeftU' colspan='5' style='padding: 7px 0px 7px 15px;'><?php echo $_SESSION['userName']; ?></td>
    </tr>
    <form name='form_change_pw' class='formNoLine' method='post' action='sc_user_info.php'>
    <tr height='50px'>
        <td class='tdGrayCH' colspan='2' style='letter-spacing: 2px;'><b>비밀번호</b></td>
        <td class='tdLeftU' colspan='5' style='padding: 7px 0px 7px 15px;'>
            <input type='password' id='now_pw' name='now_pw' class='inputNanum' style='width: 500px;' placeholder='비밀번호' required>
<?php
    if(isset($_POST['now_pw']) && isset($_POST['change_pw'])){
        $db_res = mysqli_query($db_conn, "update SC_USER set user_pw=SHA2('".$_POST['change_pw']."', 256) where user_id='".$_SESSION['userID']."' and user_pw=SHA2('".$_POST['now_pw']."', 256)");
        echo "<br>";
        if(isset($db_res)){
            echo "<font color='green'>비밀번호가 변경되었습니다.</font>";
        } else{
            echo "<font color='red'>비밀번호 변경에 실패했습니다.</font>";
        }
    }
?>
        </td>
    </tr>
    <tr>
        <td class='tdGrayCH' colspan='2' style='letter-spacing: 2px;'><label for='signup_pw'><b>변경 비밀번호</b></label></td>
        <td class='tdLeftU' colspan='5' style='padding: 7px 0px 7px 15px;'>
            <input type='password' id='change_pw' name='change_pw' class='inputNanum' style='width: 500px;' placeholder='비밀번호' pattern='^([a-z0-9!@#$%^*_])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^*_]).{4,32}$' required><br/>
            사용자 비밀번호는 5~32자 사이의 영문, 숫자, 특수기호 ( !@#$%^*_ )로 이루어져야 합니다.
        </td>
    </tr>
    <tr>
        <td class='tdGrayCH' colspan='2' style='letter-spacing: 2px;'><label for='signup_pwc'><b>변경 비밀번호 확인</b></label></td>
        <td class='tdLeftU' colspan='5' style='padding: 7px 0px 7px 15px;'>
            <input type='password' id='change_pwc' name='change_pwc' class='inputNanum' style='width: 500px;' placeholder='비밀번호' required><br>
            <div id='alert_pwSuccess' hidden><font color='green'>비밀번호가 일치합니다.</font></div>
            <div id='alert_pwRefuse' hidden><font color='red'>비밀번호가 일치하지 않습니다.</font></div>
        </td>
    </tr>
    <tr><td class='tdRightU' colspan='7' style='padding: 10px 30px;'><input type='submit' id='submit' class='butWhiteH' value='확인' disabled></td></tr>
    </form>
    </table>
<script type="text/javascript">
    $(function(){
        $('div#alert_pwSuccess').hide();
        $('div#alert_pwRefuse').hide();
    });

    $('input').keyup(function(){
        var now_pw = $('input#now_pw').val();
        var change_pw = $('input#change_pw').val();
        var change_pwc = $('input#change_pwc').val();

        if(change_pw!="" || change_pwc!=""){
            if(change_pw == change_pwc){
                $('div#alert_pwSuccess').show();
                $('div#alert_pwRefuse').hide();
            }else{
                $('div#alert_pwSuccess').hide();
                $('div#alert_pwRefuse').show();
            }
        }
        if(now_pw!="" &&(change_pw!="" && change_pw==change_pwc)){
            $('#submit').removeAttr('disabled');
        }else{
            $('#submit').attr('disabled','disabled');
        }
    });
</script>
<?php
	mysqli_close($db_conn);
    require_once "sc_footer.php";
?>
