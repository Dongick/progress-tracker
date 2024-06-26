import Layout from "../../layouts/Layout";
import { useParams, Link, useNavigate } from "react-router-dom";
import { theme } from "../../styles/theme";
import StyledButton from "../../components/StyledButton";
import {
  BannerSection,
  Navbar,
  NavButton,
  MainSection,
  TitleDiv,
  MissionContent,
  MissionSubTitle,
  MissionSubContent,
  HrDivider,
} from "./MissionStyles";
import { userInfo } from "../../recoil";
import { useRecoilState } from "recoil";
import { useQuery } from "@tanstack/react-query";
import { MissionType } from "../../types";
import { getData, postData } from "../../axios";
import { useEffect } from "react";
import example2 from "../../assets/img/no-pictures.png";
import { AxiosError } from "axios";
import { ErrorResponseDataType } from "../../types";
const MissionDetail = () => {
  const { cardId } = useParams();
  const detailURL = `/mission/${cardId}/detail`;
  const confirmURL = `/mission/${cardId}/confirm-post`;
  const missionEditURL = `/mission-edit/${cardId}`;
  const navigate = useNavigate();
  const [userInfoState, setUserInfoState] = useRecoilState(userInfo);
  const fetchData = () => getData<MissionType>(`/api/mission/info/${cardId}`);
  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: ["missionDetailInfo", `${cardId}`],
    queryFn: fetchData,
  });

  useEffect(() => {
    refetch();
  }, [data]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    const axiosError = error as AxiosError<ErrorResponseDataType>;
    const errorCode = axiosError.response?.data.errorCode;
    if (errorCode === "MISSION_NOT_FOUND") {
      return (
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            height: "100%",
          }}
        >
          <h1 style={{ fontSize: "1.5rem", margin: "15px" }}>
            해당 미션이 존재하지 않습니다.
          </h1>
          <StyledButton onClick={() => navigate("/")}>홈으로 이동</StyledButton>
        </div>
      );
    }
  }

  if (!data) {
    return <div>No data available</div>;
  }
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString();
  };
  const participateHandler = async () => {
    try {
      await postData("/api/participant", { id: cardId });
      window.location.reload();
    } catch (error) {
      console.error(error);
    }
  };

  const isStartedDate = (
    <p>
      ⏱ 미션 진행일 : {formatDate(data.startDate)} - {formatDate(data.deadline)}{" "}
      ({data?.duration} 일간)
    </p>
  );
  return (
    <Layout>
      <BannerSection>
        {data.photoUrl ? (
          <img
            src={data.photoUrl}
            alt="img"
            style={{
              width: "20%",
              height: "90%",
              marginRight: "30px",
              borderRadius: "10px",
            }}
          />
        ) : (
          <img
            src={example2}
            alt="img"
            style={{
              width: "15%",
              height: "80%",
              marginRight: "30px",
              borderRadius: "10px",
            }}
          />
        )}
        <TitleDiv>
          <div style={{ marginBottom: "10px" }}>{data.title}</div>
          <div>
            <p style={{ marginRight: "10px" }}>
              미션 생성일 : {formatDate(data.createdAt)} &nbsp;/
            </p>
            {data.status === "CREATED" ? (
              <p>⏱ 멤버모집이 완료되어야 시작됩니다!</p>
            ) : (
              isStartedDate
            )}
          </div>
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              width: "100%",
            }}
          >
            <p>인증주기: {data.frequency}</p>
            <p>👨‍👧‍👧최소 필요인원: {data.minParticipants}</p>
            <p>👨‍👧‍👧현재 참가인원: {data.participants}</p>
          </div>
          {data.participant ? (
            <StyledButton
              bgcolor={theme.subGray}
              style={{
                margin: "20px 0px 0px 0px",
                fontSize: "large",
                borderRadius: "10px",
                padding: "15px 20px",
                width: "100%",
                cursor: "auto",
              }}
            >
              이미 참가한 미션입니다!
            </StyledButton>
          ) : (
            <StyledButton
              bgcolor={theme.subGreen}
              style={{
                margin: "20px 0px 0px 0px",
                fontSize: "large",
                borderRadius: "10px",
                padding: "15px 20px",
                width: "100%",
              }}
              onClick={() => {
                if (!userInfoState.isLoggedIn)
                  window.alert("로그인을 해주세요!");
                else {
                  participateHandler();
                }
              }}
            >
              미션 참가하기
            </StyledButton>
          )}
        </TitleDiv>
      </BannerSection>
      <Navbar>
        <Link to={detailURL}>
          <NavButton clicked="true">🔎 미션 소개</NavButton>
        </Link>
        <Link to={confirmURL}>
          <NavButton clicked="false">📜 미션 인증글</NavButton>
        </Link>
      </Navbar>
      <MainSection>
        {userInfoState.user_id === data.username && data.startDate === null && (
          <StyledButton
            style={{ position: "absolute", right: "10px", top: "10px" }}
            bgcolor={theme.subGray2}
            color="white"
            onClick={() => navigate(missionEditURL)}
          >
            수정하기
          </StyledButton>
        )}
        <MissionContent>
          <h1
            style={{
              fontFamily: "gmarket2",
              fontSize: "1.3rem",
              paddingTop: "20px",
            }}
          >
            {data.username} 님이 만든 미션
          </h1>
          <h2 style={{ paddingTop: "15px", fontSize: "1.2rem" }}>
            {data.status === "CREATED" && (
              <div>
                <span
                  style={{ fontFamily: "notoBold", color: `${theme.subGreen}` }}
                >
                  {(data.minParticipants ?? 0) - (data?.participants ?? 0)}명
                </span>
                이 더 참가시 미션 시작 🚩
              </div>
            )}
            {data.status === "STARTED" && !data.participant && (
              <div>
                <span
                  style={{ fontFamily: "notoBold", color: `${theme.subGreen}` }}
                >
                  미션
                </span>
                에 참가해보세요! 🚩
              </div>
            )}
          </h2>
          <HrDivider />
          <div>
            <div>
              <MissionSubTitle>⚫ 미션 상세 소개</MissionSubTitle>
              <MissionSubContent>{data?.description}</MissionSubContent>
            </div>
          </div>
          <HrDivider />
        </MissionContent>
      </MainSection>
    </Layout>
  );
};
export default MissionDetail;
