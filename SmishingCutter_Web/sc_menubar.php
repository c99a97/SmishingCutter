<?php
	// 로그인이 안 된 상태라면
	if(empty($_SESSION[userID])){
		echo "<script>location.href='./sc_user_login.php';</script>";
	}
?>
	<table class='tbNoBorder' align='center' style='margin-left:auto; margin-right:auto;'>
	<tr style='background-color: #D5D5D5' height='25'>
		<td class='tdCenter' colspan='2' width='225'>
			<p class='pHs' style='color:#404040'>" 스미싱커터, 관리자 페이지 "</p>
		</td>
		<td class='tdRight' colspan='5' width='1300' style='padding-right: 10px;'>
			<p class='pH'>
				<a href='sc_user_info.php'>내 정보</a><font color='gray'> | </font>
				<?php echo $_SESSION[userPosition]." "; ?>
				<?php echo $_SESSION[userName]; ?>
			</p>
			<p class='pHs'> 님 환영합니다!&nbsp</p>
			<button type='button' class='butWhite' onclick="location.href='sc_user_logout.php'"><p class='pHs'>로그아웃</p></button>
		</td>
	</tr>
	<tr height='50px'>
		<td class='tdCenter' colspan='2'><a href='sc_index.php'><img src='img/smishingCutter.jpg' alt='smishingCutter' width='200' height='35'></a></td>
		<td class='tdRight' colspan='5' style='vertical-align: bottom; padding-right: 10px;'>
