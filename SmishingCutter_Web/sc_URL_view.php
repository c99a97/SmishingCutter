<?php
	require_once "sc_header.php";
	require_once "sc_DB_conn.php";
	require_once "sc_menubar.php";
?>
<?php
	// 존재하는 URL인지 확인
	$_GET['URL_no'] = mysqli_real_escape_string($db_conn, $_GET['URL_no']);
	$_GET['is_block'] = mysqli_real_escape_string($db_conn, $_GET['is_block']);
	$db_res = mysqli_query($db_conn, "select URL_no, is_block, URL, modification_time, sender_number, receiver_number, report_num, file_name from SC_URL where URL_no=".$_GET['URL_no']);
    $db_row = mysqli_fetch_row($db_res);
	if(empty($db_row)){
        echo "<script>alert('비정상적인 페이지입니다. 다시 시도해주세요.');</script>";
        echo "<script>location.href='./sc_index.php';</script>";
	} else if('0'<=$_GET['is_block'] && $_GET['is_block']<'3'){
		$db_res = mysqli_query($db_conn, "update SC_URL set is_block='".$_GET['is_block']."' where URL_no=".$_GET['URL_no']);
		mysqli_fetch_row($db_res);
		$db_row[1]=$_GET['is_block'];
	}
	// 해당 파일 코드 불러오기
	$fname = "/home/ubuntu/sftp/savePage/".$db_row[7].($_GET['org']=="on"?".html":"");
	$fd = fopen($fname, "r");
	if($fd==null){
		echo "<script>alert('비정상적인 페이지입니다. 다시 시도해주세요.');</script>";
		echo "<script>location.href='./sc_index.php';</script>";
	}
?>
<?php
	// 이전, 다음, 삭제 버튼 출력부분
	$db_res = mysqli_query($db_conn, "select URL_no from SC_URL limit 1");
	$db_min = mysqli_fetch_row($db_res);
	$db_res = mysqli_query($db_conn, "select URL_no from SC_URL order by URL_no desc limit 1");
	$db_max = mysqli_fetch_row($db_res);

	$db_next = null; $db_before = null;
	// 이전URL 버튼
	for($i=$db_row[0]-1; $i>=$db_min[0]; $i++){
		$db_res = mysqli_query($db_conn, "select URL_no from SC_URL where URL_no=".$i);
		$db_before = mysqli_fetch_row($db_res);
		if(isset($db_before)) break;
	}
	echo "<button type='button' class='butWhiteH' ";
	if(isset($db_before)){
		echo "onclick=\"location.href='./sc_URL_view.php?URL_no=".$db_before[0]."'\">";
	} else{
		echo "disabled>";
	}
	echo "< 이전</button>";
	// 다음URL 버튼
	for($i=$db_row[0]+1; $i<=$db_max[0]; $i++){
		$db_res = mysqli_query($db_conn, "select URL_no from SC_URL where URL_no=".$i);
		$db_next = mysqli_fetch_row($db_res);
		if(isset($db_next))	break;
	}
	echo " <button type='button' class='butWhiteH' ";
	if(isset($db_next)){
		echo "onclick=\"location.href='./sc_URL_view.php?URL_no=".$db_next[0]."'\">";
	} else{
		echo "disabled>";
	}
	echo "다음 ></button>";
?>
			<button type='button' class='butWhiteH' onclick="location.href='./sc_index.php'">목록</button>
			<button type='button' class='butWhiteH' onclick="url_delete();">삭제</button>
<script>
	function url_delete(){
		if(window.confirm("정말 삭제하시겠습니까?")){
			location.href="./sc_URL_delete.php?URL_no="+<?php echo $_GET['URL_no']; ?>;
		}
	}
</script>
		</td>
	</tr>
    <tr height='25px'>
        <th class='thGrayC' width='100' style='letter-spacing: 4px;'>순번</th>
        <th class='thGrayC' width='75' style='letter-spacing: 1px;'>분류(변경)</th>
        <th class='thGrayC' width='650' style='letter-spacing: 8px;'>URL</th>
        <th class='thGrayC' width='250' style='letter-spacing: 4px;'>일자</th>
        <th class='thGrayC' width='175' style='letter-spacing: 3px;'>발신번호</th>
        <th class='thGrayC' width='175' style='letter-spacing: 3px;'>수신번호</th>
        <th class='thGrayC' width='100' style='letter-spacing: 3px;'>신고누적</th>
    </tr>
	<tr>
<?php
	for($i=0; $i<7; $i++){
		if($i==1){
			echo "<td class='tdCenterU'>";
			echo "<form name='is_block' class='formNoLine' method='GET' action='sc_URL_view.php'>";
			echo "<input type='hidden' name='URL_no' value='".$_GET['URL_no']."'>";
			echo "<select onchange='this.form.submit()' id='is_block' name='is_block'>";
			echo "<option style='color: #EE0022;' value='0'>차단</option>";
			echo "<option style='color: #40C355;' value='1'".($db_row[1]=='1'?"selected":"").">허가</option>";
			echo "<option style='color: #4E9FD8;' value='2'".($db_row[1]=='2'?"selected":"").">안전</option>";
			echo "</select>";
			echo "</form>";
			echo "</td>";
		} else{
			echo "<td class='tdCenterU'";
			if($i==2)
				echo " width='650px' style='overflow:hidden;'";
			echo ">".$db_row[$i]."</td>";
		}
	}
?>
	</tr>
	<tr><th class='thGrayC' colspan='7' width='1525' style='letter-spacing: 3px;'><?php echo "(토글가능) <a href='./sc_URL_view.php?URL_no=".$_GET['URL_no'].($_GET['org']=="on"?"":"&org=on")."'>파일명 ".$db_row[7]."</a>"; ?></th></tr>
	<tr>
		<td class='tdLeft' style='border: 2px solid #D5D5D5;' colspan='7' width='1520'>
<?php
	echo "<pre style='white-space: pre-wrap; table-layout:fixed; word-wrap: break-word; white-space: -moz-pre-wrap; white-space: -pre-wrap; white-space: -o-pre-wrap; word-break:break-all;'>";
	while(!feof($fd)){
		$buffer=fread($fd, 1024);
		echo htmlspecialchars($buffer);
	}
	echo "</pre>";
	fclose($fd);
?>
		</td>
	</tr>
	</table>
<?php
	mysqli_close($db_conn);
	require_once "sc_footer.php";
?>
