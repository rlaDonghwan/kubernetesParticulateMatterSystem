
## 프로젝트 요약문
이번 프로젝트는 스프링 부트를 기반으로 실시간 미세먼지 정보를 수집하고 처리하는 시스템을 개발하는 것을 목표로 합니다. 수집된 미세먼지 정보는 네이버 지도 API를 통해 시각적으로 표현되어 사용자가 지역별 미세먼지 상태를 쉽게 확인할 수 있습니다. 이 프로젝트는 다중 데이터베이스 관리, 외부 미세먼지 정보 API 연동, 네이버 지도 API를 통한 정보 시각화, 그리고 Kubernetes를 통한 시스템의 안정적인 배포와 운영을 포함하며, 실시간 환경 정보 제공의 효율성과 유지 보수 관리 향상을 증대시킵니다.

## 1. 프로젝트 목적
이번 프로젝트는 로컬 환경의 데이터베이스에서 Kubernetes를 활용한 컨테이너 오케스트레이션 시스템으로 전환함으로써 로컬 데이터베이스 환경의 한계를 극복하고, 자동화된 복구 및 재배포 기능을 통해 서비스의 중단 없는 운영을 보장하는 것을 목적으로 합니다. 마이크로 서비스 아키텍처(MSA) 트렌드에 발맞추어 개별적으로 분산되고 독립적으로 운영될 수 있는 서비스를 개발하여 서비스의 모듈화를 촉진하고, DevOps 문화 내에서 빠른 반복과 지속적인 서비스 향상을 가능하게 합니다.

## 2. 프로젝트 목표 및 기대효과

### 2-1. 프로젝트 목표
이 프로젝트의 주된 목표는 Kubernetes를 활용하여 자가 치유 기능과 함께 클라우드 환경에서의 중단 없는 서비스 운영을 목표로 하며, 기존의 로컬 시스템이나 단일 서버 구조에서 발생할 수 있는 문제점들을 극복하고자 합니다. 하드웨어 장애나 소프트웨어 오류가 발생해도 시스템이 자동으로 복구되어 서비스 중단 시간을 최소화합니다. 사용자의 요구나 트래픽 증가에 따라 유연하게 서비스를 확장할 수 있는 기능을 제공합니다. 서비스를 독립적으로 배포하고 관리할 수 있어, 전체 시스템에 영향을 주지 않고 개별 서비스의 업데이트가 가능합니다.

### 2-2. 프로젝트 기대효과
- **높은 시스템 안정성:** 프로젝트의 핵심 이점은 시스템 안정성의 향상과 자원 관리의 효율성 증대에 있습니다. K8s의 자가 치유 메커니즘은 시스템의 가용성을 극대화합니다. 서비스 운영 중에 발생할 수 있는 장애가 생겼을 때, Kubernetes는 자동으로 문제가 있는 컨테이너를 감지하고 새로운 컨테이너로 대체하여 서비스 중단을 방지합니다. 이러한 접근 방식은 시스템을 항상 최적의 상태로 유지하여 효율적으로 관리할 수 있습니다.

- **하드웨어 자원 낭비 절감:** 하드웨어 자원의 낭비를 줄이는 측면에서도 이점을 제공합니다. 컨테이너화를 통해 실제 필요한 만큼의 자원만을 할당받아 사용함으로써, 물리적 서버를 여러 목적으로 분할하여 사용하는 기존 방식에 비해 훨씬 더 효율적입니다. 자동화된 시스템은 개발자의 개입을 최소화하여 오류의 가능성을 낮추고 전체적인 확장성과 유지 보수성을 향상시킵니다.

- **유연한 서비스 확장:** 사용자의 요구나 트래픽 증가에 따라 유연하게 서비스를 확장할 수 있으며, 개별 서비스의 독립적인 배포 및 관리를 통해 전체 시스템에 영향을 주지 않고 업데이트가 가능합니다.

이 프로젝트를 통해 안정적이고 효율적인 미세먼지 정보 제공 시스템을 구축하여 사용자의 편의성을 높이고, 지속적인 서비스 운영과 개선을 가능하게 합니다.

## 개발 동기
호흡기가 예민한 저의 개인적인 경험에서 시작된 이번 프로젝트는, 실시간 미세먼지 정보를 제공하여 건강 문제를 예방하고자 합니다. 관심 있는 분야에 기술을 접목시키기 위해 Kubernetes를 활용하여 미세먼지 정보 제공 시스템을 개발했습니다.

## 핵심 기능
- **외부 미세먼지 정보 API 연동:** 한국환경공단의 API를 통해 실시간 미세먼지 정보 수집
- **네이버 지도 API를 통한 정보 시각화:** 수집된 데이터를 시각적으로 표현하여 사용자 편의성 증대
- **AWS EKS를 통한 Kubernetes 배포 및 운영:** 자가 치유 기능과 함께 시스템의 안정성을 극대화하고 서비스 중단 시간을 최소화
- **지속적 통합 / 배포:** CI/CD 파이프라인을 통해 자동화된 배포를 통해 유지보수 효율성을 극대화

## 기대 효과
- **높은 시스템 안정성:** AWS EKS의 자기 복구 기능을 통해 시스템 가용성을 극대화하고, 장애 발생 시 자동으로 복구하여 서비스 중단을 방지합니다.
- **지속적 유지보수 및 업데이트:** GitHub Actions를 활용한 CI/CD 파이프라인을 통해 자동화된 배포를 구현하여, 시스템의 안정성과 신뢰성을 높이고, 새로운 기능 추가 및 버그 수정 작업을 신속하게 처리할 수 있습니다.
- **유연한 서비스 확장:** 사용자의 요구나 트래픽 증가에 따라 유연하게 서비스를 확장할 수 있으며, 개별 서비스의 독립적인 배포 및 관리를 통해 전체 시스템에 영향을 주지 않고 업데이트가 가능합니다.


##쿠버네티스 환경 설졍 명령어
#### 기본 네임스페이스 설정
```bash
kubectl ns default

#### 각 가용 영역(ap-northeast-2a, 2b, 2c, 2d)에서 첫 번째 노드의 주소를 변수에 저장
N1=$(kubectl get node --label-columns=topology.kubernetes.io/zone --selector=topology.kubernetes.io/zone=ap-northeast-2a -o jsonpath={.items[0].status.addresses[0].address})
N2=$(kubectl get node --label-columns=topology.kubernetes.io/zone --selector=topology.kubernetes.io/zone=ap-northeast-2b -o jsonpath={.items[0].status.addresses[0].address})
N3=$(kubectl get node --label-columns=topology.kubernetes.io/zone --selector=topology.kubernetes.io/zone=ap-northeast-2c -o jsonpath={.items[0].status.addresses[0].address})
N4=$(kubectl get node --label-columns=topology.kubernetes.io/zone --selector=topology.kubernetes.io/zone=ap-northeast-2d -o jsonpath={.items[0].status.addresses[0].address})

#### 각 노드 주소를 환경 변수로 설정하여 /etc/profile에 저장
echo "export N1=$N1" >> /etc/profile
echo "export N2=$N2" >> /etc/profile
echo "export N3=$N3" >> /etc/profile
echo "export N4=$N4" >> /etc/profile

#### 노드 주소 출력
echo $N1, $N2, $N3, $N4

#### 각 노드에 필요한 패키지 설치
ssh ec2-user@$N1 sudo yum install links tree jq tcpdump sysstat -y
ssh ec2-user@$N2 sudo yum install links tree jq tcpdump sysstat -y
ssh ec2-user@$N3 sudo yum install links tree jq tcpdump sysstat -y
ssh ec2-user@$N4 sudo yum install links tree jq tcpdump sysstat -y

#### 보안 그룹 설정
NGSGID=$(aws ec2 describe-security-groups --filters Name=group-name,Values=*ng1* --query "SecurityGroups[*].[GroupId]" --output text)
aws ec2 authorize-security-group-ingress --group-id $NGSGID --protocol '-1' --cidr 192.168.1.100/32

#### IRSA 계정 생성
eksctl create iamserviceaccount \
  --name ebs-csi-controller-sa \
  --namespace kube-system \
  --cluster ${CLUSTER_NAME} \
  --attach-policy-arn arn:aws:iam::aws:policy/service-role/AmazonEBSCSIDriverPolicy \
  --approve \
  --role-only \
  --role-name AmazonEKS_EBS_CSI_DriverRole

#### IRSA 권한 확인
eksctl get iamserviceaccount --cluster ${CLUSTER_NAME}

#### EBS 드라이버 설치 및 권한 확인
eksctl create addon --name aws-ebs-csi-driver \
  --cluster ${CLUSTER_NAME} \
  --service-account-role-arn arn:aws:iam::${ACCOUNT_ID}:role/AmazonEKS_EBS_CSI_DriverRole \
  --force

kubectl get pod -n kube-system -l app=ebs-csi-controller -o jsonpath='{.items[0].spec.containers[*].name}' ; echo

kubectl get daemonset -n kube-system -l app.kubernetes.io/name=aws-ebs-csi-driver -o jsonpath='{.items[0].spec.template.spec.containers[*].name}' ; echo

kubectl get csinodes

#### VPC ID 조회
VPC_ID=$(aws ec2 describe-vpcs --query 'Vpcs[*].{VpcId:VpcId}' --output text)
echo "VPC_ID=$VPC_ID"

#### 퍼블릭 서브넷 CIDR 블록 설정
PUBLIC_SUBNETS_CIDR=("192.168.1.0/24" "192.168.2.0/24" "192.168.3.0/24" "192.168.4.0/24")

#### 프라이빗 서브넷 CIDR 블록 설정
PRIVATE_SUBNETS_CIDR=("192.168.11.0/24" "192.168.12.0/24" "192.168.13.0/24" "192.168.14.0/24")

#### 서브넷 정보 조회
SUBNET_IDS=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPC_ID" --query 'Subnets[*].[SubnetId,CidrBlock]' --output text)
echo "SUBNET_IDS=$SUBNET_IDS"

#### 퍼블릭 서브넷에 태그 추가
for CIDR in "${PUBLIC_SUBNETS_CIDR[@]}"; do
  echo "Processing CIDR: $CIDR"
  SUBNET_ID=$(echo "$SUBNET_IDS" | grep "$CIDR" | awk '{print $1}')
  echo "SUBNET_ID=$SUBNET_ID"
  if [ -n "$SUBNET_ID" ]; then
    aws ec2 create-tags --resources $SUBNET_ID --tags Key=kubernetes.io/role/elb,Value=1
    echo "Added tag to $SUBNET_ID"
  fi
done

#### 프라이빗 서브넷에 태그 추가
for CIDR in "${PRIVATE_SUBNETS_CIDR[@]}"; do
  echo "Processing CIDR: $CIDR"
  SUBNET_ID=$(echo "$SUBNET_IDS" | grep "$CIDR" | awk '{print $1}')
  echo "SUBNET_ID=$SUBNET_ID"
  if [ -n "$SUBNET_ID" ]; then
    aws ec2 create-tags --resources $SUBNET_ID --tags Key=kubernetes.io/role/internal-elb,Value=1
    echo "Added tag to $SUBNET_ID"
  fi
done

#### 모든 서브넷의 태그 확인
aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPC_ID" --query 'Subnets[*].{ID:SubnetId,Tags:Tags}' --output table

#### 로드 밸런서 컨트롤러 설치
eksctl utils associate-iam-oidc-provider --region ap-northeast-2 --cluster myeks --approve

eksctl create iamserviceaccount \
  --region ap-northeast-2 \
  --name aws-load-balancer-controller \
  --namespace kube-system \
  --cluster myeks \
  --attach-policy-arn arn:aws:iam::(IAM계정번호):policy/AWSLoadBalancerControllerIAMPolicy \
  --approve

#### Helm 리포지토리 추가 및 업데이트
helm repo add eks https://aws.github.io/eks-charts
helm repo update

#### NLB Load Balancer Controller 설치
helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  --namespace kube-system \
  --set clusterName=myeks \
  --set region=ap-northeast-2 \
  --set vpcId=$VPC_ID \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller

#### 서비스 파일 생성 및 배포 (외부 IP 할당)
cat <<EOF > myapp-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: myapp-service
  namespace: default
spec:
  selector:
    app: myapp
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8081
  type: LoadBalancer
EOF

kubectl apply -f myapp-service.yaml

#### 외부 IP 확인
kubectl get svc myapp-service -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'

#### 로드 밸런서 웹훅 서비스 확인
kubectl get endpoints -n kube-system aws-load-balancer-webhook-service
kubectl get svc -n kube-system aws-load-balancer-webhook-service
kubectl get pods -n kube-system -l app.kubernetes.io/name=aws-load-balancer-controller

#### helm 상태 확인
helm status aws-load-balancer-controller -n kube-system
helm list -n kube-system

#### 쿠브 옵스 뷰 설치
helm repo add geek-cookbook https://geek-cookbook.github.io/charts/
helm repo update
helm install kube-ops-view geek-cookbook/kube-ops-view --version 1.2.2 --set env.TZ="Asia/Seoul" --namespace kube-system

kubectl patch svc -n kube-system kube-ops-view -p '{"spec":{"type":"LoadBalancer"}}'

kubectl get svc -n kube-system kube-ops-view -o jsonpath={.status.loadBalancer.ingress[0].hostname} | awk '{ print "KUBE-OPS-VIEW URL = http://"$1":8080/#scale=1.5"}'
