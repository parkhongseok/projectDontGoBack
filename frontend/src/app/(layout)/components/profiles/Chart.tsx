"use client";

import { useState, useEffect, useCallback } from "react";
import {
  ResponsiveContainer,
  ComposedChart,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  CartesianGrid,
  Bar,
  Line,
  Cell,
} from "recharts";

import { Button, ButtonGroup, Spinner } from "react-bootstrap";
import { httpRequest } from "../../utils/httpRequest";
import * as Types from "../../utils/types";
import { BACKEND_API_URL } from "../../utils/globalValues";

// Chart 컴포넌트가 받을 Props 타입 정의
interface ChartProps {
  userId: string;
}

const renderLegendText = (value: string) => {
  // '자산 변동액'이라는 텍스트에만 fontGray3 클래스를 적용
  if (value === "자산 변동액") {
    return <span className="fontGray3">{value}</span>;
  }
  // 나머지 텍스트는 기본 스타일로 표시
  return <span>{value}</span>;
};

// Y축 레이블을 간결하게 포맷하는 함수 (음수 지원)
const formatYAxis = (tickItem: number) => {
  // 0은 그대로 0을 반환
  if (tickItem === 0) {
    return "0";
  }
  // 숫자의 절댓값이 10,000 이상인지 확인
  if (Math.abs(tickItem) >= 10000) {
    // 나눗셈은 부호를 그대로 유지하므로, 결과에 '만'을 붙여 반환
    return `${tickItem / 10000}만`;
  }
  // 10,000 미만의 숫자는 그대로 문자열로 변환
  return tickItem.toString();
};

export default function Chart({ userId }: ChartProps) {
  // 컴포넌트 상태 정의
  const [data, setData] = useState<Types.AssetHistoryPoint[]>([]);
  const [interval, setInterval] = useState<"daily" | "weekly" | "monthly">("daily");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 데이터 페칭 함수
  const fetchChartData = useCallback(
    async (currentInterval: string) => {
      setLoading(true);
      setError(null);

      // 날짜 범위 설정 (예시: 'weekly'는 최근 1년)
      // 이 부분은 필요에 따라 더 정교하게 만들 수 있습니다.
      const to = new Date();
      const from = new Date();
      if (currentInterval === "daily") {
        from.setMonth(to.getMonth() - 1); // 최근 1개월
      } else {
        from.setFullYear(to.getFullYear() - 1); // 최근 1년
      }

      // YYYY-MM-DD 형식으로 변환
      const fromDate = from.toISOString().split("T")[0];
      const toDate = to.toISOString().split("T")[0];

      const url = `${BACKEND_API_URL}/v1/users/${userId}/asset-histories?from=${fromDate}&to=${toDate}&interval=${currentInterval}`;

      const success = (result: Types.ResData<Types.AssetHistorySeries>) => {
        setData(result.data.points);
        setLoading(false);
      };

      const fail = () => {
        setError("차트 데이터를 불러오는 데 실패했습니다.");
        setLoading(false);
      };

      httpRequest("GET", url, null, success, fail);
    },
    [userId]
  );

  // 컴포넌트 마운트 또는 interval 변경 시 데이터 다시 불러오기
  useEffect(() => {
    if (userId) {
      fetchChartData(interval);
    }
  }, [userId, interval, fetchChartData]);

  // 로딩 중 UI
  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ height: "250px" }}>
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      </div>
    );
  }

  // 에러 발생 시 UI
  if (error) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ height: "250px" }}>
        <p className="text-danger">{error}</p>
      </div>
    );
  }

  return (
    <div className="px-3 mb-4">
      {/* 시간 간격 선택 버튼 */}
      <div className="d-flex justify-content-end mb-2">
        <ButtonGroup size="sm">
          <Button
            variant={interval === "daily" ? "primary" : "outline-secondary"}
            onClick={() => setInterval("daily")}
          >
            일별
          </Button>
          <Button
            variant={interval === "weekly" ? "primary" : "outline-secondary"}
            onClick={() => setInterval("weekly")}
          >
            주별
          </Button>
          <Button
            variant={interval === "monthly" ? "primary" : "outline-secondary"}
            onClick={() => setInterval("monthly")}
          >
            월별
          </Button>
        </ButtonGroup>
      </div>

      {/* 차트 렌더링 영역 */}
      <div style={{ width: "100%", height: 250 }}>
        <ResponsiveContainer>
          <ComposedChart data={data} margin={{ top: 5, right: 20, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" fontSize={12} />
            <YAxis
              yAxisId="left"
              orientation="left"
              stroke="var(--green)"
              fontSize={12}
              tickFormatter={formatYAxis}
            />
            <YAxis
              yAxisId="right"
              orientation="right"
              stroke="var(--text-color-blue)"
              fontSize={12}
              tickFormatter={formatYAxis}
            />
            <Tooltip />

            {/* formatter 속성 추가 */}
            <Legend formatter={renderLegendText} />

            {/* Bar 컴포넌트에서는 className 제거 */}
            <Bar
              yAxisId="right"
              dataKey="changeAmount"
              name="자산 변동액"
              fill="var(--text-color-blue)"
            >
              {data.map((entry, index) => (
                <Cell
                  key={`cell-${index}`}
                  fill={entry.type === "RED" ? "var(--text-color-red)" : "var(--text-color-blue)"}
                />
              ))}
            </Bar>

            <Line
              yAxisId="left"
              type="monotone"
              dataKey="amount"
              stroke="var(--green)"
              name="자산 총액"
              strokeWidth={2}
              dot={false}
            />
          </ComposedChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
